package com.example.omg_project.domain.joinpost.dto;

import com.example.omg_project.domain.joinpost.entity.JoinPostComment;
import com.example.omg_project.domain.joinpost.entity.JoinPostReply;
import com.example.omg_project.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Request, Response DTO 클래스를 하나로 묶어 Inner Static Class로 한 번에 관리
public class JoinPostReplyDto {
    // Request DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private String content;

        // DTO -> Entity 변환 메서드
        public JoinPostReply toEntity(User user, JoinPostComment joinPostComment) {
            return JoinPostReply.builder()
                    .content(this.content)
                    .user(user)
                    .joinPostComment(joinPostComment)
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
        private Long joinPostCommentId;

        // Entity -> DTO 변환 메서드
        public static Response fromEntity(JoinPostReply joinPostReply) {
            return Response.builder()
                    .id(joinPostReply.getId())
                    .content(joinPostReply.getContent())
                    .createdAt(joinPostReply.getCreatedAt())
                    .userId(joinPostReply.getUser().getId())
                    .joinPostCommentId(joinPostReply.getJoinPostComment().getId())
                    .build();
        }
    }
}