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
import com.example.omg_project.global.jwt.util.JwtTokenizer;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
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

import static com.example.omg_project.global.jwt.util.JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class UserApiController {

    private final JwtTokenizer jwtTokenizer;
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
        log.info("로그인 요청이 들어왔습니다.");
        log.info("아이디    :: {}", userLoginDto.getUsername());
        log.info("비밀번호 :: {}", userLoginDto.getPassword());

        if(bindingResult.hasErrors()){ // 필드 에러 확인
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        Optional<User> user = userServiceimpl.findByUsername(userLoginDto.getUsername());

        // 비밀번호 일치여부 체크
        if(!passwordEncoder.matches(userLoginDto.getPassword(), user.get().getPassword())) {
            return new ResponseEntity("비밀번호가 올바르지 않습니다.",HttpStatus.UNAUTHORIZED);
        }

        List<String> roles = user.get().getRoles().stream().map(Role::getName).collect(Collectors.toList());

        refreshTokenService.deleteRefreshToken(String.valueOf(user.get().getId()));

        // 토큰 발급
        String accessToken = jwtTokenizer.createAccessToken(user.get().getId(), user.get().getUsername(), user.get().getName(),  roles);
        String refreshToken = jwtTokenizer.createRefreshToken(user.get().getId(), user.get().getUsername(), user.get().getName(), roles);

        // 리프레시 토큰 디비 저장
        Date date = new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_COUNT);
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setValue(refreshToken);
        refreshTokenEntity.setUserId(user.get().getId());
        refreshTokenEntity.setExpiration(date.toString());

        refreshTokenService.addRefreshToken(refreshTokenEntity);

        // 토큰 쿠키 저장
        Cookie accessTokenCookie = new Cookie("accessToken",accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(Math.toIntExact(jwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT/1000));

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(Math.toIntExact(REFRESH_TOKEN_EXPIRE_COUNT/1000));

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        // 응답 값
        UserLoginResponseDto loginResponseDto = UserLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.get().getId())
                .username(user.get().getUsername())
                .build();

        return new ResponseEntity(loginResponseDto, HttpStatus.OK);
    }

    /**
     * 로그아웃 요청
     */
    @GetMapping("/logout")
    public void logout(@CookieValue(name = "accessToken", required = false) String accessToken,
                       @CookieValue(name = "refreshToken", required = false) String refreshToken,
                       HttpServletResponse response) {
        log.info("로그아웃 요청이 들어왔습니다.");
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

        Date expirationTime = Jwts.parser()
                .setSigningKey(jwtTokenizer.getAccessSecret())
                .parseClaimsJws(jwt)
                .getBody()
                .getExpiration();

        log.info("accessToken 만료시간 :: {}" , expirationTime);

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

        // tokens 데이터 삭제
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
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserSignUpDto userSignUpDto) {
        try {
            userServiceimpl.signUp(userSignUpDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공");
        } catch (RuntimeException e) {
            log.error("회원가입 실패 :: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원가입 실패 :: " + e.getMessage());
        }
    }
}
