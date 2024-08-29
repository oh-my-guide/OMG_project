package com.example.omg_project.domain.reviewpost.dto;

import com.example.omg_project.domain.reviewpost.entity.PlaceReview;
import com.example.omg_project.domain.reviewpost.entity.ReviewPost;
import com.example.omg_project.domain.trip.entity.TripLocation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PlaceReviewDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        private Long tripLocationId;
        private Long reviewPostId;
        private String content;

        // DTO -> 엔티티
        public PlaceReview toEntity(TripLocation tripLocation, ReviewPost reviewPost) {
            return PlaceReview.builder()
                    .tripLocation(tripLocation)
                    .reviewPost(reviewPost)
                    .content(this.content)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private Long tripLocationId;
        private Long reviewPostId;
        private String content;

        // 엔티티 -> DTO
        public static Response fromEntity(PlaceReview placeReview) {
            return Response.builder()
                    .id(placeReview.getId())
                    .tripLocationId(placeReview.getTripLocation().getId())
                    .reviewPostId(placeReview.getReviewPost().getId())
                    .content(placeReview.getContent())
                    .build();
        }
    }
}