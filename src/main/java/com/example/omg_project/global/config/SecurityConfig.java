package com.example.omg_project.global.config;

import com.example.omg_project.global.jwt.exception.CustomAuthenticationEntryPoint;
import com.example.omg_project.global.jwt.filter.JWTFilter;
import com.example.omg_project.global.jwt.service.RedisBlackTokenService;
import com.example.omg_project.global.jwt.service.RedisRefreshTokenService;
import com.example.omg_project.global.jwt.util.JwtTokenizer;
import com.example.omg_project.global.oauth2.handle.CustomSuccessHandler;
import com.example.omg_project.global.oauth2.service.CustomOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final RedisBlackTokenService redisBlackTokenService;
    private final RedisRefreshTokenService redisRefreshTokenService;
    private final JwtTokenizer jwtTokenizer;
    private final CustomOauth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;

    // 모든 유저 허용 페이지
    String[] allAllowPage = new String[] {
            "/",        // 메인페이지
            "/signup", // 회원가입 페이지
            "/signin", // 로그인 페이지
            "/css/**", "/js/**", "/files/**", // css, js, 이미지 url
            "/api/users/login", // 로그인 페이지
            "/api/users/signup", // 회원가입 페이지
            "/api/users/mail","/api/users/verify-code", "/api/users/check-email","/api/users/check-usernick", // 인증 메일 페이지
            "/oauth2/**", "/login/oauth2/**", // OAuth2 로그인 허용
            "/api/users/randomNickname", // 랜덤 닉네임 생성
            "/api/users/reset-password", "/api/users/verify-temporary-password", "/users/reset-user-password", // 임시 비밀번호 발급
            "/service",     // 프로젝트 소개 글
            "/reviewPost/", // 여행 후기 게시글 목록
            "/joinPost/",    // 일행 모집 게시글 목록
            "/api/weather",  // 날씨 API
            "/faq",          // 고객센터
            "/api/weather/coords",
            "/api/channel"
    };

    // 관리자 페이지
    String[] adminAllowPage = new String[] {
            "/admin",
            "/admin/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorize -> authorize
                        .requestMatchers(allAllowPage).permitAll()
                        .requestMatchers(adminAllowPage).hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JWTFilter(jwtTokenizer, redisBlackTokenService, redisRefreshTokenService), UsernamePasswordAuthenticationFilter.class) // JWT 필터 사용
                .formLogin(form -> form.disable()) // 로그인 폼 비활성화
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 관리 Stateless 설정(서버가 클라이언트 상태 저장x)
                .csrf(csrf -> csrf.disable()) // cors 허용
                .httpBasic(httpBasic -> httpBasic.disable()) // http 기본 인증(헤더) 비활성화
                .cors(cors -> cors.configurationSource(configurationSource()))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(customAuthenticationEntryPoint)
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/signin")
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler)
                        .failureUrl("/loginFailure")
                        .authorizationEndpoint(authorization -> authorization.baseUri("/oauth2/authorization"))
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource configurationSource(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*"); // 모든 도메인 허용
        config.addAllowedOrigin("http://localhost:3000"); // 프론트의 주소
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowedMethods(List.of("GET", "POST", "DELETE"));
        config.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));
        config.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}