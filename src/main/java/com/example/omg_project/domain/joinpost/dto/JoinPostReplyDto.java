package com.example.omg_project.domain.joinpost.dto;

import com.example.omg_project.domain.joinpost.entity.JoinPostComment;
import com.example.omg_project.domain.joinpost.entity.JoinPostReply;
import com.example.omg_project.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Request, Response DTO 클래스를 하나로 묶어 Inner Static Class로 한 번에 관리
public class JoinPostReplyDto {
    // Request DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private Long userId;
        private String content;
        private boolean secret;
        private LocalDateTime createdAt;    // 엔티티에 now()가 되어있지만 null로 들어가는 이슈가 발생하여 추가함.

        // DTO -> Entity 변환 메서드
        public JoinPostReply toEntity(User user, JoinPostComment joinPostComment) {
            return JoinPostReply.builder()
                    .content(this.content)
                    .user(user)
                    .joinPostComment(joinPostComment)
                    .secret(this.secret)
                    .createdAt(LocalDateTime.now())
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
        private Long joinPostCommentId;
        private String usernick;

        // Entity -> DTO 변환 메서드
        public static Response fromEntity(JoinPostReply joinPostReply) {
            return Response.builder()
                    .id(joinPostReply.getId())
                    .content(joinPostReply.getContent())
                    .secret(joinPostReply.isSecret())
                    .createdAt(joinPostReply.getCreatedAt())
                    .userId(joinPostReply.getUser().getId())
                    .joinPostCommentId(joinPostReply.getJoinPostComment().getId())
                    .usernick(joinPostReply.getUser().getUsernick())
                    .build();
        }

        // 날짜를 원하는 형식으로 변환
        public String getFormattedCreatedAt() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
            return createdAt.format(formatter);
        }
    }
}