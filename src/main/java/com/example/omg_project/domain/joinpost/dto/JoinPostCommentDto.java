package com.example.omg_project.domain.joinpost.dto;

import com.example.omg_project.domain.joinpost.entity.JoinPost;
import com.example.omg_project.domain.joinpost.entity.JoinPostComment;
import com.example.omg_project.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

// Request, Response DTO 클래스를 하나로 묶어 Inner Static Class로 한 번에 관리
public class JoinPostCommentDto {
    // Request DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private String content;

        // DTO -> Entity 변환 메서드
        public JoinPostComment toEntity(User user, JoinPost joinPost) {
            return JoinPostComment.builder()
                    .content(this.content)
                    .user(user)
                    .joinPost(joinPost)
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
        private LocalDateTime createdAt;
        private Long userId;
        private Long joinPostId;

        // Entity -> DTO 변환 메서드
        public static Response fromEntity(JoinPostComment joinPostComment) {
            return Response.builder()
                    .id(joinPostComment.getId())
                    .content(joinPostComment.getContent())
                    .createdAt(joinPostComment.getCreatedAt())
                    .userId(joinPostComment.getUser().getId())
                    .joinPostId(joinPostComment.getJoinPost().getId())
                    .build();
        }
    }
}