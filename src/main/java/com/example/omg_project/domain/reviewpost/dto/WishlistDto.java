package com.example.omg_project.domain.reviewpost.dto;

import com.example.omg_project.domain.reviewpost.entity.ReviewPost;
import com.example.omg_project.domain.reviewpost.entity.Wishlist;
import com.example.omg_project.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class WishlistDto {

    // Request DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private Long reviewPostId;
        private Long userId;

        // DTO -> Entity 변환 메서드
        public Wishlist toEntity(User user, ReviewPost reviewPost) {
            return Wishlist.builder()
                    .user(user)
                    .reviewPost(reviewPost)
                    .build();
        }
    }

    // Response DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private Long reviewPostId;
        private Long userId;
        private String reviewPostTitle;
        private String userNick;
        private Long tripId;  // tripId 필드 추가

        // Entity -> DTO 변환 메서드
        public static Response fromEntity(Wishlist wishlist) {
            return Response.builder()
                    .id(wishlist.getId())
                    .reviewPostId(wishlist.getReviewPost().getId())
                    .userId(wishlist.getUser().getId())
                    .reviewPostTitle(wishlist.getReviewPost().getTitle())
                    .userNick(wishlist.getUser().getUsernick())
                    .tripId(wishlist.getReviewPost().getTrip().getId())
                    .build();
        }
    }
}