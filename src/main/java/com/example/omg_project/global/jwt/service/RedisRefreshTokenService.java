package com.example.omg_project.global.jwt.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisRefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private final String REDIS_REFRESH_TOKEN_KEY_PREFIX = "refreshToken:";

    public RedisRefreshTokenService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 리프레시 토큰 저장
     */
    public void addRefreshToken(String refreshToken, long expiration) {
        String key = REDIS_REFRESH_TOKEN_KEY_PREFIX + refreshToken;
        redisTemplate.opsForValue().set(key, refreshToken, expiration, TimeUnit.MILLISECONDS);
    }

    /**
     * 리프레시 토큰이 유효한지 확인
     */
    public boolean isRefreshTokenValid(String refreshToken) {
        String key = REDIS_REFRESH_TOKEN_KEY_PREFIX + refreshToken;
        String storedUserId = redisTemplate.opsForValue().get(key);
        return storedUserId != null;
    }

    /**
     * 리프레시 토큰 삭제
     */
    public void deleteRefreshToken(String refreshToken) {
        String key = REDIS_REFRESH_TOKEN_KEY_PREFIX + refreshToken;
        redisTemplate.delete(key);
    }

    /**
     * 리프레시 토큰 조회
     */
    public String getRefreshToken(String refreshToken) {
        String key = REDIS_REFRESH_TOKEN_KEY_PREFIX + refreshToken;
        return redisTemplate.opsForValue().get(key);
    }
}