package com.example.omg_project.domain.reviewpost.entity;

import com.example.omg_project.domain.trip.entity.TripLocation;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "place_review")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trip_location_id", nullable = false)
    private TripLocation tripLocation;

    @ManyToOne
    @JoinColumn(name = "review_post_id", nullable = false)
    private ReviewPost reviewPost;

    @Column(nullable = false, length = 500)
    private String content;

}
