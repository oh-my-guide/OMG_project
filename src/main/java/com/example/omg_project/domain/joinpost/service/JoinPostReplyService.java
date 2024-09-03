package com.example.omg_project.domain.joinpost.service;

import com.example.omg_project.domain.joinpost.dto.JoinPostReplyDto;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface JoinPostReplyService {
    // 일행 모집 대댓글 작성
    JoinPostReplyDto.Response createReply(Long commentId, Long userId, JoinPostReplyDto.Request replyRequest) throws JsonProcessingException;
    // 일행 모집 대댓글 조회
    List<JoinPostReplyDto.Response> findAllByCommentId(Long commentId);
    // 일행 모집 대댓글 수정
    JoinPostReplyDto.Response updateReply(Long replyId, JoinPostReplyDto.Request replyRequest);
    // 일행 모집 대댓글 삭제
    void deleteReply(Long replyId);
    void deleteByUserId(Long userId);
}
