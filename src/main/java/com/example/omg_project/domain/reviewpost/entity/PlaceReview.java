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

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    // JPA의 영속성 컨텍스트 덕분에 entity 객체의 값만 변경하면 자동으로 변경사항 반영함 -> repository.update 를 쓰지 않아도 됨!
    public void updateContent(String content) {
        this.content = content;
    }

}
