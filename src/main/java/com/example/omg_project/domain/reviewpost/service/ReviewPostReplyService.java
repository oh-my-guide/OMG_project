package com.example.omg_project.domain.reviewpost.service;

import com.example.omg_project.domain.reviewpost.dto.ReviewPostReplyDto;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface ReviewPostReplyService {
    // 후기 대댓글 작성
    ReviewPostReplyDto.Response createReply(Long commentId, Long userId, ReviewPostReplyDto.Request replyRequest) throws JsonProcessingException;
    // 후기 대댓글 조회
    List<ReviewPostReplyDto.Response> findAllByCommentId(Long commentId);
    // 후기 대댓글 수정
    ReviewPostReplyDto.Response updateReply(Long replyId, ReviewPostReplyDto.Request replyRequest);
    // 후기 대댓글 삭제
    void deleteReply(Long replyId);
    void deleteByUserId(Long userId);
}
