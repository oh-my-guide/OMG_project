package com.example.omg_project.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Table(name = "admin_notices")
@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // 관리자 공지사항 제목

    @Column(nullable = false)
    private String content; // 관지가 공지사항 내용

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 관리자 공지사항 작성일

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public String getFormattedCreatedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        return createdAt.format(formatter);
    }
}
