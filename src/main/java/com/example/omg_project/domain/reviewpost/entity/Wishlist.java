package com.example.omg_project.domain.reviewpost.entity;

import com.example.omg_project.domain.user.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "wishlists")
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    private ReviewPost reviewPost;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
