package com.example.omg_project.domain.joinpost.dto;

import com.example.omg_project.domain.joinpost.entity.JoinPost;
import com.example.omg_project.domain.joinpost.entity.JoinPostComment;
import com.example.omg_project.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Request, Response DTO 클래스를 하나로 묶어 Inner Static Class로 한 번에 관리
public class JoinPostCommentDto {
    // Request DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private Long userId;
        private String content;
        private boolean secret;

        // DTO -> Entity 변환 메서드
        public JoinPostComment toEntity(User user, JoinPost joinPost) {
            return JoinPostComment.builder()
                    .content(this.content)
                    .user(user)
                    .joinPost(joinPost)
                    .secret(this.secret)
                    .build();
        }
    }

    // Response DTO
    @Getter
    @Builder
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String content;
        private boolean secret;
        private LocalDateTime createdAt;
        private Long userId;
        private Long joinPostId;
        private String usernick;

        // Entity -> DTO 변환 메서드
        public static Response fromEntity(JoinPostComment joinPostComment) {
            return Response.builder()
                    .id(joinPostComment.getId())
                    .content(joinPostComment.getContent())
                    .secret(joinPostComment.isSecret())
                    .createdAt(joinPostComment.getCreatedAt())
                    .userId(joinPostComment.getUser().getId())
                    .joinPostId(joinPostComment.getJoinPost().getId())
                    .usernick(joinPostComment.getUser().getUsernick())
                    .build();
        }

        // 날짜를 원하는 형식으로 변환
        public String getFormattedCreatedAt() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
            return createdAt.format(formatter);
        }
    }
}