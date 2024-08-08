package com.example.omg_project.global.jwt.service;

import com.example.omg_project.global.jwt.entity.JwtBlacklist;
import com.example.omg_project.global.jwt.repository.JwtBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtBlacklistService {

    private final JwtBlacklistRepository jwtBlacklistRepository;

    public void save(JwtBlacklist blacklist) {
        jwtBlacklistRepository.save(blacklist);
    }

    public boolean isTokenBlacklisted(String token) {
        return jwtBlacklistRepository.existsByToken(token);
    }
}
