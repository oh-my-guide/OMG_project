package com.example.omg_project.domain.joinpost.controller;

import com.example.omg_project.domain.joinpost.dto.JoinPostCommentDto;
import com.example.omg_project.domain.joinpost.service.JoinPostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/joinPosts")
@RequiredArgsConstructor
public class JoinPostCommentApiController {
    private final JoinPostCommentService joinPostCommentService;

    /**
     * 일행 모집 댓글 등록
     */
    @PostMapping("/{postId}/comments")
    public ResponseEntity<JoinPostCommentDto.Response> createComment(@PathVariable Long postId, @RequestParam Long userId, @RequestBody JoinPostCommentDto.Request commentRequest) {
        JoinPostCommentDto.Response comment = joinPostCommentService.createComment(postId, userId, commentRequest);
        return ResponseEntity.ok(comment);
    }

    /**
     * 일행 모집 댓글 조회
     */
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<JoinPostCommentDto.Response>> getAllComments(@PathVariable Long postId) {
        List<JoinPostCommentDto.Response> allByPostId = joinPostCommentService.findAllByPostId(postId);
        return ResponseEntity.ok(allByPostId);
    }

    /**
     * 일행 모집 댓글 수정
     */
    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<JoinPostCommentDto.Response> updateComment(@PathVariable Long commentId, @RequestBody JoinPostCommentDto.Request commentRequest) {
        JoinPostCommentDto.Response updateComment = joinPostCommentService.updateComment(commentId, commentRequest);
        return ResponseEntity.ok(updateComment);
    }

    /**
     * 일행 모집 댓글 삭제
     */
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        joinPostCommentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }


}
