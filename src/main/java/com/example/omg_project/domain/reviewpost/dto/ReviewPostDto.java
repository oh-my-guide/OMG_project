package com.example.omg_project.domain.reviewpost.dto;

import com.example.omg_project.domain.reviewpost.entity.ReviewPost;
import com.example.omg_project.domain.trip.dto.ReadTripDTO;
import com.example.omg_project.domain.trip.entity.Trip;
import com.example.omg_project.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReviewPostDto {
    // Request DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private String title;
        private String content;
        private Long userId;
        private Long tripId;

        // DTO -> 엔티티
        public ReviewPost toEntity(User user, Trip trip) {
            return ReviewPost.builder()
                    .title(this.title)
                    .content(this.content)
                    .user(user)
                    .trip(trip)
                    .build();
        }
    }

    // Response DTO
    @Getter
    @Builder
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String title;
        private String content;
        private int views;
        private LocalDateTime createdAt;
        private Long userId;
        private String username;
        private String usernick;
        private ReadTripDTO trip;

        // 엔티티 -> DTO
        public static Response fromEntity(ReviewPost reviewPost) {
            return Response.builder()
                    .id(reviewPost.getId())
                    .title(reviewPost.getTitle())
                    .content(reviewPost.getContent())
                    .views(reviewPost.getViews())
                    .createdAt(reviewPost.getCreatedAt())
                    .userId(reviewPost.getUser().getId())
                    .usernick(reviewPost.getUser().getUsernick())
                    .username(reviewPost.getUser().getUsername())
                    .trip(ReadTripDTO.fromEntity(reviewPost.getTrip()))
                    .build();
        }

        // 날짜를 원하는 형식으로 변환
        public String getFormattedCreatedAt() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
            return createdAt.format(formatter);
        }
    }
}