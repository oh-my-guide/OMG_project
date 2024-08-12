package com.example.omg_project.domain.reviewpost.entity;

import com.example.omg_project.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "review_posts_comments")
@Getter
@Setter
@NoArgsConstructor
public class ReviewPostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "review_post_id", nullable = false)
    private ReviewPost reviewPost;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "reviewPostComment", cascade = CascadeType.ALL)
    private List<ReviewPostReply> reviewPostReplies;
}
