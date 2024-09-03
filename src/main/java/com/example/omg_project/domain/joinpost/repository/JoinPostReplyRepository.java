package com.example.omg_project.domain.joinpost.repository;

import com.example.omg_project.domain.joinpost.entity.JoinPostComment;
import com.example.omg_project.domain.joinpost.entity.JoinPostReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JoinPostReplyRepository extends JpaRepository<JoinPostReply, Long> {
    // 특정 댓글에 대한 모든 대댓글 조회
    List<JoinPostReply> findAllByJoinPostCommentId(Long joinPostCommentId);
    List<JoinPostReply> findByUserId(Long userId);
}
