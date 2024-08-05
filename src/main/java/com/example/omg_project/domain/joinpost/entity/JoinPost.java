package com.example.omg_project.domain.joinpost.entity;

import com.example.omg_project.domain.trip.entity.Trip;
import com.example.omg_project.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "join_posts")
@Getter
@Setter
@NoArgsConstructor
public class JoinPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
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

    @OneToMany(mappedBy = "joinPost", cascade = CascadeType.ALL)
    private List<JoinPostComment> comments;

    @OneToMany(mappedBy = "joinPost", cascade = CascadeType.ALL)
    private List<JoinPostLike> likes;
}
