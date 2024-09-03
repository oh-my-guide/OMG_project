package com.example.omg_project.domain.reviewpost.repository;

import com.example.omg_project.domain.joinpost.entity.JoinPostReply;
import com.example.omg_project.domain.reviewpost.entity.ReviewPostReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewPostReplyRepository extends JpaRepository<ReviewPostReply, Long> {
    // 특정 댓글에 대한 모든 대댓글 조회
    List<ReviewPostReply> findAllByReviewPostCommentId(Long reviewPostCommentId);
    List<ReviewPostReply> findByUserId(Long userId);
}
