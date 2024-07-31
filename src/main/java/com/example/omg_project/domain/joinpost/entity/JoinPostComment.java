package com.example.omg_project.domain.joinpost.entity;

import com.example.omg_project.domain.reviewpost.entity.ReviewPostReply;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "join_post_comments")
@Getter
@Setter
@NoArgsConstructor
public class JoinPostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long user;

    @ManyToOne
    @JoinColumn(name = "join_post_id", nullable = false)
    private JoinPost joinPost;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "joinPostComment", cascade = CascadeType.ALL)
    private List<JoinPostReply> joinPostReplies;
}
