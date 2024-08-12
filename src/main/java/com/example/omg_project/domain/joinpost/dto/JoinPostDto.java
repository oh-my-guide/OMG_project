package com.example.omg_project.domain.joinpost.dto;

import com.example.omg_project.domain.joinpost.entity.JoinPost;
import com.example.omg_project.domain.trip.dto.ReadTripDTO;
import com.example.omg_project.domain.trip.entity.Trip;
import com.example.omg_project.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JoinPostDto {
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
        public JoinPost toEntity(User user, Trip trip) {
            return JoinPost.builder()
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
        private LocalDateTime createdAt;
        private Long userId;
        private String username;
        private String usernick;
        private ReadTripDTO trip;

        // 엔티티 -> DTO
        public static Response fromEntity(JoinPost joinPost) {
            return Response.builder()
                    .id(joinPost.getId())
                    .title(joinPost.getTitle())
                    .content(joinPost.getContent())
                    .createdAt(joinPost.getCreatedAt())
                    .userId(joinPost.getUser().getId())
                    .usernick(joinPost.getUser().getUsernick())
                    .username(joinPost.getUser().getUsername())
                    .trip(ReadTripDTO.fromEntity(joinPost.getTrip()))
                    .build();
        }

        // 날짜를 원하는 형식으로 변환
        public String getFormattedCreatedAt() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
            return createdAt.format(formatter);
        }
    }
}