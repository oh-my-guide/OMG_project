package com.example.omg_project.global.jwt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 리프레시 토큰은 엑세스 토큰을 재발급 해주는 용도
 * 리프레시 토큰을 담는 엔티티
 */
@Entity
@Table(name = "refresh_token")
@Getter @Setter
public class RefreshToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "value", length = 512)
    private String value; // 리프레시 토큰 값

    @Column(name = "user_id")
    private Long userId; // 로그인한 유저 아이디

    @Column(name = "expiration_time")
    private String expiration; // 만료시간
}