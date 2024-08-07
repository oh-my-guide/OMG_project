package com.example.omg_project.global.jwt.filter;

import com.example.omg_project.global.jwt.exception.JwtExceptionCode;
import com.example.omg_project.global.jwt.service.JwtBlacklistService;
import com.example.omg_project.global.jwt.service.RefreshTokenService;
import com.example.omg_project.global.jwt.token.JwtAuthenticationToken;
import com.example.omg_project.global.jwt.util.JwtTokenizer;
import com.example.omg_project.global.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 요청이 들어올 때마다 JWT 토큰을 검증하는 필터
 * 토큰을 검증하고 유효한 사용자라면 그 사용자의 정보를 SecurityContextHolder 에 설정
 */
@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final JwtTokenizer jwtTokenizer;
    private final JwtBlacklistService jwtBlacklistService;
    private final RefreshTokenService refreshTokenService;

    public JWTFilter(JwtTokenizer jwtTokenizer, JwtBlacklistService jwtBlacklistService, RefreshTokenService refreshTokenService) {
        this.jwtTokenizer = jwtTokenizer;
        this.jwtBlacklistService = jwtBlacklistService;
        this.refreshTokenService = refreshTokenService;
    }

    /**
     * 접근 허용 앤드 포인트 목록
     */
    private static final List<String> PERMIT_ALL_PATHS = List.of(
            "/signup", "/signin", "/", "/api/users/login", "/api/users/signup",
            "/api/users/mail","/api/users/verify-code", "/api/users/check-email","/api/users/check-usernick"
    );

    /**
     * 필터 메서드
     * 각 요청마다 JWT 토큰을 검증하고 인증을 설정
     * @param request       요청 객체
     * @param response      응답 객체
     * @param filterChain   필터 체인
     * @throws ServletException 서블릿 예외
     * @throws IOException      입출력 예외
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getRequestURI(); // 요청 경로 확인
        if (isPermitAllPath(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getToken(request); // 요청에서 토큰을 추출

        if (!StringUtils.hasText(token)) { // 토큰을 사용하여 인증 설정
            handleMissingToken(request, response);
        } else {
            handleTokenValidation(request, response, token);
        }
        filterChain.doFilter(request, response); // 다음 필터로 요청을 전달
        // log.info("토큰 제대로?? : {}", token);
    }

    /**
     *  요청 경로가 인증 없이 접근 가능한지 확인
     */
    private boolean isPermitAllPath(String requestPath) {
        return PERMIT_ALL_PATHS.stream().anyMatch(requestPath::matches);
    }

    /**
     * Access Token이 없는 경우 처리하는 메서드
     */
    private void handleMissingToken(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 쿠키에서 Refresh Token을 얻어옴
        String refreshToken = getRefreshToken(request);
        if (StringUtils.hasText(refreshToken)) {
            try {
                // Refresh Token이 DB에 존재하는지 확인
                if (refreshTokenService.isRefreshTokenValid(refreshToken)) {
                    if (!jwtTokenizer.isRefreshTokenExpired(refreshToken)) {
                        String newAccessToken = jwtTokenizer.refreshAccessToken(refreshToken);
                        setAccessTokenCookie(response, newAccessToken);
                        getAuthentication(newAccessToken);
                    } else {
                        // Refresh Token이 만료된 경우
                        handleException(request, JwtExceptionCode.EXPIRED_TOKEN, "Refresh token expired");
                    }
                } else {
                    // Refresh Token이 DB에 없는 경우
                    handleException(request, JwtExceptionCode.NOT_FOUND_TOKEN, "Refresh token not found in database");
                }
            } catch (ExpiredJwtException e) {
                // Refresh Token이 만료된 경우
                handleException(request, JwtExceptionCode.EXPIRED_TOKEN, "Expired refresh token", e);
            }
        } else {
            handleException(request, JwtExceptionCode.NOT_FOUND_TOKEN, "Token not found in request");
        }
    }

    /**
     * Access Token이 있는 경우 처리하는 메서드
     */
    private void handleTokenValidation(HttpServletRequest request, HttpServletResponse response, String token) throws ServletException, IOException {
        try {
            // 토큰이 블랙리스트에 있는지 확인
            if (jwtBlacklistService.isTokenBlacklisted(token)) {
                handleException(request, JwtExceptionCode.BLACKLISTED_TOKEN, "Token is blacklisted: " + token);
            } else {
                getAuthentication(token);
            }
        } catch (ExpiredJwtException e) {
            handleExpiredAccessToken(request, response, token, e);
        } catch (UnsupportedJwtException e) {
            handleException(request, JwtExceptionCode.UNSUPPORTED_TOKEN, "Unsupported token: " + token, e);
        } catch (MalformedJwtException e) {
            handleException(request, JwtExceptionCode.INVALID_TOKEN, "Invalid token: " + token, e);
        } catch (IllegalArgumentException e) {
            handleException(request, JwtExceptionCode.NOT_FOUND_TOKEN, "Token not found: " + token, e);
        } catch (Exception e) {
            handleException(request, JwtExceptionCode.UNKNOWN_ERROR, "JWT filter internal error: " + token, e);
        }
    }

    /**
     * Access Token이 만료된 경우 처리하는 메서드
     */
    private void handleExpiredAccessToken(HttpServletRequest request, HttpServletResponse response, String token, ExpiredJwtException e) throws ServletException, IOException {
        log.warn("Access token expired: {}", token);
        String refreshToken = getRefreshToken(request);
        if (StringUtils.hasText(refreshToken) && !jwtTokenizer.isRefreshTokenExpired(refreshToken)) {
            // Refresh Token이 유효한 경우 새로운 Access Token 발급
            String newAccessToken = jwtTokenizer.refreshAccessToken(refreshToken);

            setAccessTokenCookie(response, newAccessToken);

            getAuthentication(newAccessToken);
        } else {
            handleException(request, JwtExceptionCode.EXPIRED_TOKEN, "Expired Token : " + token, e);
        }
    }

    /**
     * 요청에서 토큰 추출
     */
    private String getToken(HttpServletRequest request) {

        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }

        Cookie[] cookies = request.getCookies(); // 쿠키에서 토큰을 찾음
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue(); // accessToken 쿠키에서 토큰 반환
                }
            }
        }
        return null; // 토큰을 찾지 못한 경우 null 반환
    }

    /**
     * 쿠키에서 Refresh Token을 추출하는 메서드
     */
    private String getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 새로운 Access Token을 쿠키에 설정하는 메서드
     */
    private void setAccessTokenCookie(HttpServletResponse response, String newAccessToken) {
        Cookie accessTokenCookie = new Cookie("accessToken", newAccessToken);
        accessTokenCookie.setHttpOnly(true); // XSS 보호를 위해 HttpOnly 설정
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(Math.toIntExact(jwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT / 1000));
        response.addCookie(accessTokenCookie);
    }

    /**
     * 토큰을 사용하여 인증 설정
     */
    private void getAuthentication(String token) {
        Claims claims = jwtTokenizer.parseAccessToken(token); // 토큰에서 클레임을 파싱
        String username = claims.getSubject(); // 이메일을 가져옴
        Long userId = claims.get("userId", Long.class);  // 사용자 ID를 가져옴
        String name = claims.get("name", String.class); // 이름을 가져옴
        List<GrantedAuthority> authorities = getGrantedAuthorities(claims); // 사용자 권한을 가져옴

        CustomUserDetails userDetails = new CustomUserDetails(username, "", authorities);

        Authentication authentication = new JwtAuthenticationToken(authorities, userDetails, null); // 인증 객체 생성

        SecurityContextHolder.getContext().setAuthentication(authentication); // SecurityContextHolder에 인증 객체 설정

    }

    /**
     * JWT Claims에서 권한 정보를 추출하는 메서드
     */
    private List<GrantedAuthority> getGrantedAuthorities(Claims claims) {
        List<String> roles = claims.get("roles", List.class);
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * 예외 처리 메서드
     */
    private void handleException(HttpServletRequest request, JwtExceptionCode exceptionCode, String logMessage) {
        handleException(request, exceptionCode, logMessage, null);
    }

    private void handleException(HttpServletRequest request, JwtExceptionCode exceptionCode, String logMessage, Exception e) {
        request.setAttribute("exception", exceptionCode.getCode());
        log.error(logMessage, e);
//        throw new BadCredentialsException(logMessage, e);
        log.info("로그인을 부탁드립니다.");
    }
}