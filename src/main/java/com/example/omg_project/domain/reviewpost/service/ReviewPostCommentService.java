package com.example.omg_project.domain.reviewpost.service;

import com.example.omg_project.domain.reviewpost.dto.ReviewPostCommentDto;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface ReviewPostCommentService {
    // 후기 댓글 등록
    ReviewPostCommentDto.Response createComment(Long postId, Long userId, ReviewPostCommentDto.Request commentRequest) throws JsonProcessingException;
    // 후기 댓글 조회
    List<ReviewPostCommentDto.Response> findAllByPostId(Long postId);
    // 후기 댓글 수정
    ReviewPostCommentDto.Response updateComment(Long commentId,ReviewPostCommentDto.Request commentRequest);
    // 후기 댓글 삭제
    void deleteComment(Long commentId);
    void deleteByUserId(Long userId);
}
