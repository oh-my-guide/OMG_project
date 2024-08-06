package com.example.omg_project.domain.user.controller;

import com.example.omg_project.domain.role.entity.Role;
import com.example.omg_project.domain.user.dto.UserLoginDto;
import com.example.omg_project.domain.user.dto.UserLoginResponseDto;
import com.example.omg_project.domain.user.dto.UserSignUpDto;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.service.impl.UserServiceImpl;
import com.example.omg_project.global.jwt.entity.JwtBlacklist;
import com.example.omg_project.global.jwt.entity.RefreshToken;
import com.example.omg_project.global.jwt.service.JwtBlacklistService;
import com.example.omg_project.global.jwt.service.RefreshTokenService;
import com.example.omg_project.global.jwt.util.JWTUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.omg_project.global.jwt.util.JWTUtil.REFRESH_TOKEN_EXPIRE_COUNT;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class UserApiController {

    private final JWTUtil jwtUtil;
    private final UserServiceImpl userServiceimpl;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final JwtBlacklistService jwtBlackListService;

    /**
     * 로그인 요청 시 jwt 토큰 발급
     */
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid UserLoginDto userLoginDto,
                                BindingResult bindingResult,
                                HttpServletResponse response){
        System.out.println("로그인 요청이 들어왓습니다.");
        System.out.println("아이디 :: " + userLoginDto.getUsername());
        System.out.println("비밀번호 :: " + userLoginDto.getPassword());

        if(bindingResult.hasErrors()){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        Optional<User> user = userServiceimpl.findByUsername(userLoginDto.getUsername());

        if(!passwordEncoder.matches(userLoginDto.getPassword(), user.get().getPassword())) {
            return new ResponseEntity("비밀번호가 올바르지 않습니다.",HttpStatus.UNAUTHORIZED);
        }

        List<String> roles = user.get().getRoles().stream().map(Role::getName).collect(Collectors.toList());

        refreshTokenService.deleteRefreshToken(String.valueOf(user.get().getId()));

        String accessToken = jwtUtil.createAccessToken(
                user.get().getId(), user.get().getUsername(), user.get().getName(),  roles);

        String refreshToken = jwtUtil.createRefreshToken(
                user.get().getId(), user.get().getUsername(), user.get().getName(), roles);

        Date date = new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_COUNT);

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setValue(refreshToken);
        refreshTokenEntity.setUserId(user.get().getId());
        refreshTokenEntity.setExpiration(date.toString());

        refreshTokenService.addRefreshToken(refreshTokenEntity);

        UserLoginResponseDto loginResponseDto = UserLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.get().getId())
                .username(user.get().getUsername())
                .build();

        Cookie accessTokenCookie = new Cookie("accessToken",accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(Math.toIntExact(jwtUtil.ACCESS_TOKEN_EXPIRE_COUNT/1000)); //30분

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(Math.toIntExact(REFRESH_TOKEN_EXPIRE_COUNT/1000)); //7일

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        return new ResponseEntity(loginResponseDto, HttpStatus.OK);
    }

    /**
     * 엑세스 토큰이 만료되면 리프레시 토큰으로 재발급
     */
    @PostMapping("/refreshToken")
    public ResponseEntity refreshToken(HttpServletRequest request, HttpServletResponse response){

        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                if("refreshToken".equals(cookie.getName())){
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if(refreshToken == null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        Claims claims = jwtUtil.parseRefreshToken(refreshToken);
        Long userId = Long.valueOf ((Integer)claims.get("userId"));

        User user = userServiceimpl.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자를 찾지 못했습니다."));

        List roles = (List)claims.get("roles");

        String accessToken = jwtUtil.createAccessToken(userId, user.getUsername(), user.getName(),  roles);

        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(Math.toIntExact( jwtUtil.ACCESS_TOKEN_EXPIRE_COUNT / 1000));

        response.addCookie(accessTokenCookie);

        UserLoginResponseDto responseDto = UserLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .userId(user.getId())
                .build();

        return new ResponseEntity(responseDto, HttpStatus.OK);
    }

    /**
     * 로그아웃 요청
     */
    @GetMapping("/logout")
    public void logout(@CookieValue(name = "accessToken", required = false) String accessToken,
                       @CookieValue(name = "refreshToken", required = false) String refreshToken,
                       HttpServletResponse response) {
        System.out.println("로그아웃 요청이 들어왔습니다.");
        if (accessToken == null) {

            try {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Access token not found in cookies.");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        String jwt = accessToken;
        System.out.println("jwt: " + jwt);

        Date expirationTime = Jwts.parser()
                .setSigningKey(jwtUtil.getAccessSecret())
                .parseClaimsJws(jwt)
                .getBody()
                .getExpiration();
        System.out.println("만료시간: " + expirationTime);

        JwtBlacklist blacklist = new JwtBlacklist(jwt, expirationTime);
        jwtBlackListService.save(blacklist);

        SecurityContextHolder.clearContext();

        Cookie accessCookie = new Cookie("accessToken", null);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);
        response.addCookie(accessCookie);

        Cookie refresCcookie = new Cookie("refreshToken", null);
        refresCcookie.setPath("/");
        refresCcookie.setMaxAge(0);
        response.addCookie(refresCcookie);

        refreshTokenService.deleteRefreshToken(refreshToken);

        try {
            response.sendRedirect("/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 회원가입
     */
    @GetMapping("/signup")
    public ResponseEntity<UserSignUpDto> getSignupForm() {
        UserSignUpDto userSignUpDto = new UserSignUpDto();
        return ResponseEntity.ok(userSignUpDto);
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserSignUpDto userSignUpDto) {
        try {
            userServiceimpl.signUp(userSignUpDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공!!");
        } catch (RuntimeException e) {
            log.error("회원가입 실패!! :: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원가입 실패!! :: " + e.getMessage());
        }
    }

    /**
     * 회원 탈퇴
     */
}
