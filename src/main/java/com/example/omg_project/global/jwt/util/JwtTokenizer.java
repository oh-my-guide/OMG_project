package com.example.omg_project.global.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

/**
 * JWT 생성 / 검증 과정에 해당하는 유틸리티 클래스
 */
@Component
@Slf4j
@Getter
public class JwtTokenizer {
    private final byte[] accessSecret;
    private final byte[] refreshSecret;

    public static Long ACCESS_TOKEN_EXPIRE_COUNT = 30 * 60 * 1000L; // 30분
    public static Long REFRESH_TOKEN_EXPIRE_COUNT = 7 * 24 * 60 * 60 * 1000L; // 7일

    // .yml 파일에 secretKey, refreshKey 값이 있어야 한다.
    public JwtTokenizer(@Value("${jwt.secretKey}") String accessSecret,
                        @Value("${jwt.refreshKey}") String refreshSecret){
        this.accessSecret = accessSecret.getBytes(StandardCharsets.UTF_8);
        this.refreshSecret = refreshSecret.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * AccessToken 생성
     */
    public String createAccessToken(Long id, String username, String name, List<String> roles) {
        return createToken(id, username, name, roles, ACCESS_TOKEN_EXPIRE_COUNT, accessSecret);
    }

    /**
     * RefreshToken 생성
     */
    public String createRefreshToken(Long id, String username, String name, List<String> roles) {
        return createToken(id, username, name, roles, REFRESH_TOKEN_EXPIRE_COUNT, refreshSecret);
    }

    /**
     * Jwts 빌더를 사용하여 token 생성
     */
    private String createToken(Long id, String username, String name, List<String> roles, long expireCount, byte[] secret) {
        Claims claims = Jwts.claims().setSubject(username); // 기본으로 가지고 있는 claim : subject
        claims.put("userId", id);
        claims.put("name", name);
        claims.put("roles", roles);

        Date now = new Date();
        Date expiration = new Date(now.getTime() + expireCount);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, getSigningKey(secret))
                .compact();
    }

    //----------------------------------------------------------------------------------추가-------------------------------//
    /**
     * JWT 토큰에서 사용자 ID 추출
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token, accessSecret);
        return claims.get("userId", Long.class);
    }

    /**
     * JWT 토큰에서 사용자 아이디 추출
     * @param token JWT 토큰
     * @return 사용자 아이디(이메일)
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token, accessSecret);
        return claims.getSubject();
    }

    /**
     * JWT 토큰에서 역할 정보 추출
     * @param token JWT 토큰
     * @return 역할 목록
     */
    public List<String> getRolesFromToken(String token) {
        Claims claims = parseToken(token, accessSecret);
        return claims.get("roles", List.class);
    }

//---------------------------------------------------------------------------------------------------------------------//
    /**
     * access token 파싱
     * @param accessToken access token
     * @return 파싱된 토큰
     */
    public Claims parseAccessToken(String accessToken) {
        return parseToken(accessToken, accessSecret);
    }

    /**
     * refresh token 파싱
     * @param refreshToken refresh token
     * @return 파싱된 토큰
     */
    public Claims parseRefreshToken(String refreshToken) {
        return parseToken(refreshToken, refreshSecret);
    }

    /**
     * token 파싱
     * @param token access/refresh token
     * @param secretKey access/refresh 비밀키
     * @return 파싱된 토큰
     */
    public Claims parseToken(String token, byte[] secretKey) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * @param secretKey - byte형식
     * @return Key 형식 시크릿 키
     */
    public static Key getSigningKey(byte[] secretKey) {
        return Keys.hmacShaKeyFor(secretKey);
    }

    /**
     * 토큰이 만료되었는지 확인하는 메서드
     */
    public boolean isTokenExpired(String token, byte[] secretKey) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey(secretKey)).build().parseClaimsJws(token).getBody();
            return false;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * 액세스 토큰이 만료되었는지 확인
     */
    public boolean isAccessTokenExpired(String accessToken) {
        return isTokenExpired(accessToken, accessSecret);
    }

    /**
     * 리프레시 토큰이 만료되었는지 확인
     */
    public boolean isRefreshTokenExpired(String refreshToken) {
        return isTokenExpired(refreshToken, refreshSecret);
    }

    /**
     * 리프레시 토큰으로 새로운 액세스 토큰을 발급
     */
    public String refreshAccessToken(String refreshToken) {
        Claims claims = parseRefreshToken(refreshToken);
        Long userId = claims.get("userId", Long.class);

        String username = claims.getSubject(); // 토큰의 주체
        String name = claims.get("name", String.class);
        List<String> roles = (List<String>) claims.get("roles");
        return createAccessToken(userId, username, name, roles);
    }
}