package com.example.omg_project.domain.joinpost.service;

import com.example.omg_project.domain.joinpost.dto.JoinPostCommentDto;
import com.example.omg_project.domain.reviewpost.entity.ReviewPostComment;

import java.util.List;

public interface JoinPostCommentService {
    // 일행 댓글 등록
    JoinPostCommentDto.Response createComment(Long postId, Long userId, JoinPostCommentDto.Request commentRequest);
    // 일행 댓글 조회
    List<JoinPostCommentDto.Response> findAllByPostId(Long postId);
    // 일행 댓글 수정
    JoinPostCommentDto.Response updateComment(Long commentId,JoinPostCommentDto.Request commentRequest);
    // 일행 댓글 삭제
    void deleteComment(Long commentId);
    void deleteByUserId(Long userId);
}
