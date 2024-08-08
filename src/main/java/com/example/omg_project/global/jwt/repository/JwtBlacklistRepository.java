package com.example.omg_project.global.jwt.repository;

import com.example.omg_project.global.jwt.entity.JwtBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JwtBlacklistRepository extends JpaRepository<JwtBlacklist, Long> {

    boolean existsByToken(String token);
}