package com.example.omg_project.domain.reviewpost.repository;

import com.example.omg_project.domain.reviewpost.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findAllByUserId(Long userId);
    Wishlist findByUserIdAndReviewPostId(Long userId, Long reviewPostId);
    boolean existsByUserIdAndReviewPostId(Long userId, Long reviewPostId);
}
