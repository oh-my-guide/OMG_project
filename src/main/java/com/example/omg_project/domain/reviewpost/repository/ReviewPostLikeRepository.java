package com.example.omg_project.domain.reviewpost.repository;

import com.example.omg_project.domain.reviewpost.entity.ReviewPost;
import com.example.omg_project.domain.reviewpost.entity.ReviewPostLike;
import com.example.omg_project.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewPostLikeRepository extends JpaRepository<ReviewPostLike, Long> {
    // 좋아요 여부 확인
    boolean existsByUserIdAndReviewPostId(Long userId, Long postId);
    // 좋아요 수 조회
    int countByReviewPostId(Long postId);
    // 좋아요 가져오기
    ReviewPostLike findByUserAndReviewPost(User user, ReviewPost reviewPost);
}
