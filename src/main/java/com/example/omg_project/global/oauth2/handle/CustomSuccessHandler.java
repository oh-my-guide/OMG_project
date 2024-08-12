package com.example.omg_project.global.oauth2.handle;

import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.repository.UserRepository;
import com.example.omg_project.global.jwt.entity.RefreshToken;
import com.example.omg_project.global.jwt.service.RefreshTokenService;
import com.example.omg_project.global.jwt.util.JwtTokenizer;
import com.example.omg_project.global.oauth2.dto.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

            String username = customUserDetails.getUsername();
            Optional<User> user = userRepository.findByUsername(username);

            if (user.isEmpty()) {
                System.out.println("유저기ㅏ 없어요");
                throw new UsernameNotFoundException("User not found with username: " + username);
            }
            Long userId = user.get().getId();
            System.out.println("userId :: " + userId);
            String name = customUserDetails.getName();
            List<String> roles = customUserDetails.getRoles();

            log.info("Oauth2 로그인 성곻했습니다. ");
            log.info("jwt 토큰 생성 :: userId: {}, username: {}, name: {}, roles: {}", userId, username, name, roles);

            String accessToken = jwtTokenizer.createAccessToken(userId, username, name, roles);
            String refreshToken = jwtTokenizer.createRefreshToken(userId, username, name, roles);

            log.info("Access Token :: {}", accessToken);
            log.info("Refresh Token :: {}", refreshToken);

            // 쿠키에 토큰 저장
            Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(Math.toIntExact(jwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT / 1000));

            Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(Math.toIntExact(jwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT / 1000));

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);

            // 리프레시 토큰 DB 저장
            Date date = new Date(System.currentTimeMillis() + jwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT);
            RefreshToken refreshTokenEntity = new RefreshToken();
            refreshTokenEntity.setValue(refreshToken);
            refreshTokenEntity.setUserId(userId);
            refreshTokenEntity.setExpiration(date.toString());

            refreshTokenService.addRefreshToken(refreshTokenEntity);

            response.sendRedirect("/oauthPage"); // 로그인 성공 후 보여질 페이지 --> 나중에 수정해야해욤

        } catch (Exception e) {
            log.error("Oauth2 로그인에 실패했습니다.", e);
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred during authentication");
            }
        }
    }
}