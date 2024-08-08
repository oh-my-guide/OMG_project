package com.example.omg_project.domain.joinpost.controller;

import com.example.omg_project.domain.joinpost.dto.JoinPostReplyDto;
import com.example.omg_project.domain.joinpost.service.JoinPostReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/joinPosts/comments")
@RequiredArgsConstructor
public class JoinPostReplyApiController {
    private final JoinPostReplyService joinPostReplyService;

    /**
     * 일행 모집 대댓글 등록
     */
    @PostMapping("/{commentId}/replies")
    public ResponseEntity<JoinPostReplyDto.Response> createReply(@PathVariable Long commentId, @RequestBody JoinPostReplyDto.Request replyRequest) {
        JoinPostReplyDto.Response reply = joinPostReplyService.createReply(commentId, replyRequest.getUserId(), replyRequest);
        return ResponseEntity.ok(reply);
    }

    /**
     * 일행 모집 대댓글 조회
     */
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<JoinPostReplyDto.Response>> findAllByCommentId(@PathVariable Long commentId) {
        List<JoinPostReplyDto.Response> allByCommentId = joinPostReplyService.findAllByCommentId(commentId);
        return ResponseEntity.ok(allByCommentId);
    }

    /**
     * 일행 모집 대댓글 수정
     */
    @PutMapping("/{commentId}/replies/{replyId}")
    public ResponseEntity<JoinPostReplyDto.Response> updateReply(@PathVariable Long replyId, @RequestBody JoinPostReplyDto.Request replyRequest) {
        JoinPostReplyDto.Response updateReply = joinPostReplyService.updateReply(replyId, replyRequest);
        return ResponseEntity.ok(updateReply);
    }

    /**
     * 일행 모집 대댓글 삭제
     */
    @DeleteMapping("/{commentId}/replies/{replyId}")
    public ResponseEntity<Void> deleteReply(@PathVariable Long replyId) {
        joinPostReplyService.deleteReply(replyId);
        return ResponseEntity.noContent().build();
    }

}
