package com.example.omg_project.domain.reviewpost.repository;

import com.example.omg_project.domain.reviewpost.entity.ReviewPost;
import com.example.omg_project.domain.reviewpost.entity.ReviewPostLike;
import com.example.omg_project.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewPostLikeRepository extends JpaRepository<ReviewPostLike, Long> {
    boolean existsByUserIdAndReviewPostId(Long userId, Long postId);

    int countByReviewPostId(Long postId);

    ReviewPostLike findByUserAndReviewPost(User user, ReviewPost reviewPost);
}
