package com.example.omg_project.global.jwt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * 로그아웃 된 토큰을 보여주는 엔티티
 */
@Entity
@Table(name = "blacklist_token")
@Getter @Setter
@NoArgsConstructor
public class JwtBlacklist {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", length = 512)
    private String token; // 엑세스 토큰 값

    @Column(name = "expiration_time")
    private Date expiration; // 엑세스 토큰의 만료 시간

    public JwtBlacklist(String token, Date expiration) {
        this.token = token;
        this.expiration = expiration;
    }
}