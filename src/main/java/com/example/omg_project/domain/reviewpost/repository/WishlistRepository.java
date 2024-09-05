package com.example.omg_project.domain.reviewpost.repository;

import com.example.omg_project.domain.reviewpost.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    // 찜하기 전체 조회
    List<Wishlist> findAllByUserId(Long userId);
    // 찜하기 조회
    Wishlist findByUserIdAndReviewPostId(Long userId, Long reviewPostId);
    // 찜하기 여부 확인
    boolean existsByUserIdAndReviewPostId(Long userId, Long reviewPostId);
}
