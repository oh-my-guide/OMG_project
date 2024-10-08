package com.example.omg_project.domain.reviewpost.dto;

import com.example.omg_project.domain.reviewpost.entity.ReviewPostComment;
import com.example.omg_project.domain.reviewpost.entity.ReviewPostReply;
import com.example.omg_project.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Request, Response DTO 클래스를 하나로 묶어 Inner Static Class로 한 번에 관리
public class ReviewPostReplyDto {
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
        public ReviewPostReply toEntity(User user, ReviewPostComment reviewPostComment) {
            return ReviewPostReply.builder()
                    .content(this.content)
                    .user(user)
                    .reviewPostComment(reviewPostComment)
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
        private Long reviewPostCommentId;
        private String usernick;
        private Long reviewPostCommentUserId;

        // Entity -> DTO 변환 메서드
        public static Response fromEntity(ReviewPostReply reviewPostReply) {
            return Response.builder()
                    .id(reviewPostReply.getId())
                    .content(reviewPostReply.getContent())
                    .secret(reviewPostReply.isSecret())
                    .createdAt(reviewPostReply.getCreatedAt())
                    .userId(reviewPostReply.getUser().getId())
                    .reviewPostCommentId(reviewPostReply.getReviewPostComment().getId())
                    .usernick(reviewPostReply.getUser().getUsernick())
                    .reviewPostCommentUserId(reviewPostReply.getReviewPostComment().getUser().getId())
                    .build();
        }

        // 날짜를 원하는 형식으로 변환
        public String getFormattedCreatedAt() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
            return createdAt.format(formatter);
        }
    }
}