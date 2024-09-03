package com.example.omg_project.domain.reviewpost.entity;

import com.example.omg_project.domain.trip.entity.Trip;
import com.example.omg_project.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "review_posts")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(nullable = false)
    private int views;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "reviewPost", cascade = CascadeType.ALL)
    private List<ReviewPostComment> comments;

    @OneToMany(mappedBy = "reviewPost", cascade = CascadeType.ALL)
    private List<ReviewPostLike> likes;

    @OneToMany(mappedBy = "reviewPost", cascade = CascadeType.ALL)
    private List<Wishlist> wishlists;

    @OneToMany(mappedBy = "reviewPost", cascade = CascadeType.ALL)
    private List<PlaceReview> placeReviews;

    // 엔티티가 영속화되기 전에 실행되는 메서드
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    // JPA의 영속성 컨텍스트 덕분에 entity 객체의 값만 변경하면 자동으로 변경사항 반영함 -> repository.update 를 쓰지 않아도 됨!
    public void updateContent(String title, String content) {
        this.title = title;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    // 조회수 증가
    public void incrementViews() {
        this.views++;
    }
}
