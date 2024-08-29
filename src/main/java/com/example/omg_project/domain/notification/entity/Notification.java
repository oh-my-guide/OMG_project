package com.example.omg_project.domain.notification.entity;

import com.example.omg_project.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "notification")
@Getter
@Setter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // 알림을 받을 사용자

    private String message; // 알림 내용

    private boolean isRead = false; // 읽음 여부

    private LocalDateTime createdAt;

    private String notificationType; // 댓글, 대댓글, 채팅 등 구분

    // 댓글이나 채팅 등과의 관계 매핑 (optional)
    private Long relatedEntityId; // 댓글, 대댓글, 채팅 등의 ID
}

