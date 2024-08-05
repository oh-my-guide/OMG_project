package com.example.omg_project.domain.joinpost.dto;

import com.example.omg_project.domain.joinpost.entity.JoinPost;
import com.example.omg_project.domain.trip.entity.Trip;
import com.example.omg_project.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

// Request, Response DTO 클래스를 하나로 묶어 Inner Static Class로 한 번에 관리
public class JoinPostDto {
    // Request DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private String title;
        private String content;

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

        // 엔티티 -> DTO
        public static Response fromEntity(JoinPost joinPost) {
            return Response.builder()
                    .id(joinPost.getId())
                    .title(joinPost.getTitle())
                    .content(joinPost.getContent())
                    .createdAt(joinPost.getCreatedAt())
                    .build();
        }
    }
}