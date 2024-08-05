package com.example.omg_project.domain.reviewpost.entity;

import com.example.omg_project.domain.trip.entity.Trip;
import com.example.omg_project.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "review_posts")
@Getter
@Setter
@NoArgsConstructor
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

    @Column(nullable = false, length = 500)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "reviewPost", cascade = CascadeType.ALL)
    private List<ReviewPostComment> comments;

    @OneToMany(mappedBy = "reviewPost", cascade = CascadeType.ALL)
    private List<ReviewPostLike> likes;

    @OneToMany(mappedBy = "reviewPost", cascade = CascadeType.ALL)
    private List<Wishlist> wishlists;

    @OneToMany(mappedBy = "reviewPost", cascade = CascadeType.ALL)
    private List<PlaceReview> placeReviews;
}
