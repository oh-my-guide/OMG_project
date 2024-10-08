package com.example.omg_project.domain.joinpost.entity;

import com.example.omg_project.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "join_post_likes")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinPostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "join_post_id", nullable = false)
    private JoinPost joinPost;
}
