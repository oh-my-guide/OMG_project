package com.example.omg_project.domain.reviewpost.repository;

import com.example.omg_project.domain.reviewpost.entity.ReviewPostComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewPostCommentRepository extends JpaRepository<ReviewPostComment, Long> {
    // 특정 게시글에 대한 모든 댓글 조회
    List<ReviewPostComment> findAllByReviewPostId(Long reviewPostId);
}
