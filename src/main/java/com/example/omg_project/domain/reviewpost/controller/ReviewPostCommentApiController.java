package com.example.omg_project.domain.reviewpost.controller;

import com.example.omg_project.domain.reviewpost.dto.ReviewPostCommentDto;
import com.example.omg_project.domain.reviewpost.service.ReviewPostCommentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviewPosts")
@RequiredArgsConstructor
public class ReviewPostCommentApiController {
    private final ReviewPostCommentService reviewPostCommentService;

    /**
     * 후기 댓글 등록
     */
    @PostMapping("/{postId}/comments")
    public ResponseEntity<ReviewPostCommentDto.Response> createComment(@PathVariable Long postId, @RequestBody ReviewPostCommentDto.Request commentRequest) throws JsonProcessingException {
        ReviewPostCommentDto.Response comment = reviewPostCommentService.createComment(postId, commentRequest.getUserId(), commentRequest);
        return ResponseEntity.ok(comment);
    }

    /**
     * 후기 댓글 조회
     */
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<ReviewPostCommentDto.Response>> findAllByPostId(@PathVariable Long postId) {
        List<ReviewPostCommentDto.Response> allByPostId = reviewPostCommentService.findAllByPostId(postId);
        return ResponseEntity.ok(allByPostId);
    }

    /**
     * 후기 댓글 수정
     */
    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<ReviewPostCommentDto.Response> updateComment(@PathVariable Long commentId, @RequestBody ReviewPostCommentDto.Request commentRequest) {
        ReviewPostCommentDto.Response updateComment = reviewPostCommentService.updateComment(commentId, commentRequest);
        return ResponseEntity.ok(updateComment);
    }

    /**
     * 후기 댓글 삭제
     */
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        reviewPostCommentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }


}
