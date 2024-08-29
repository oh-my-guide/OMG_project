package com.example.omg_project.domain.reviewpost.controller;

import com.example.omg_project.domain.reviewpost.dto.ReviewPostReplyDto;
import com.example.omg_project.domain.reviewpost.service.ReviewPostReplyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviewPosts/comments")
@RequiredArgsConstructor
public class ReviewPostReplyApiController {
    private final ReviewPostReplyService reviewPostReplyService;

    /**
     * 후기 대댓글 등록
     */
    @PostMapping("/{commentId}/replies")
    public ResponseEntity<ReviewPostReplyDto.Response> createReply(@PathVariable Long commentId, @RequestBody ReviewPostReplyDto.Request replyRequest) throws JsonProcessingException {
        ReviewPostReplyDto.Response reply = reviewPostReplyService.createReply(commentId, replyRequest.getUserId(), replyRequest);
        return ResponseEntity.ok(reply);
    }

    /**
     * 후기 대댓글 조회
     */
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<ReviewPostReplyDto.Response>> findAllByCommentId(@PathVariable Long commentId) {
        List<ReviewPostReplyDto.Response> allByCommentId = reviewPostReplyService.findAllByCommentId(commentId);
        return ResponseEntity.ok(allByCommentId);
    }

    /**
     * 후기 대댓글 수정
     */
    @PutMapping("/{commentId}/replies/{replyId}")
    public ResponseEntity<ReviewPostReplyDto.Response> updateReply(@PathVariable Long replyId, @RequestBody ReviewPostReplyDto.Request replyRequest) {
        ReviewPostReplyDto.Response updateReply = reviewPostReplyService.updateReply(replyId, replyRequest);
        return ResponseEntity.ok(updateReply);
    }

    /**
     * 후기 대댓글 삭제
     */
    @DeleteMapping("/{commentId}/replies/{replyId}")
    public ResponseEntity<Void> deleteReply(@PathVariable Long replyId) {
        reviewPostReplyService.deleteReply(replyId);
        return ResponseEntity.noContent().build();
    }

}
