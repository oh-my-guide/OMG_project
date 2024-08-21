package com.example.omg_project.global.jwt.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisBlackTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private final String REDIS_BLACKLIST_KEY_PREFIX = "blacklist:";

    public RedisBlackTokenService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 블랙리스트 토큰 저장
     */
    public void addBlacklistedToken(String token, long expiration) {
        String key = REDIS_BLACKLIST_KEY_PREFIX + token;
        redisTemplate.opsForValue().set(key, token, expiration, TimeUnit.MILLISECONDS);
    }

    /**
     * 블랙리스트 토큰이 유효한지 확인
     */
    public boolean isTokenBlacklisted(String token) {
        String key = REDIS_BLACKLIST_KEY_PREFIX + token;
        String storedToken = redisTemplate.opsForValue().get(key);
        return storedToken != null;
    }
}