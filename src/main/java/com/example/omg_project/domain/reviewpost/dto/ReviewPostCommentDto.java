package com.example.omg_project.domain.reviewpost.dto;

import com.example.omg_project.domain.reviewpost.entity.ReviewPost;
import com.example.omg_project.domain.reviewpost.entity.ReviewPostComment;
import com.example.omg_project.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Request, Response DTO 클래스를 하나로 묶어 Inner Static Class로 한 번에 관리
public class ReviewPostCommentDto {
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
        public ReviewPostComment toEntity(User user, ReviewPost reviewPost) {
            return ReviewPostComment.builder()
                    .content(this.content)
                    .user(user)
                    .reviewPost(reviewPost)
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
        private Long reviewPostId;
        private String usernick;

        // Entity -> DTO 변환 메서드
        public static Response fromEntity(ReviewPostComment reviewPostComment) {
            return Response.builder()
                    .id(reviewPostComment.getId())
                    .content(reviewPostComment.getContent())
                    .secret(reviewPostComment.isSecret())
                    .createdAt(reviewPostComment.getCreatedAt())
                    .userId(reviewPostComment.getUser().getId())
                    .reviewPostId(reviewPostComment.getReviewPost().getId())
                    .usernick(reviewPostComment.getUser().getUsernick())
                    .build();
        }

        // 날짜를 원하는 형식으로 변환
        public String getFormattedCreatedAt() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
            return createdAt.format(formatter);
        }
    }
}