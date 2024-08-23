package com.example.omg_project.domain.reviewpost.controller;

import com.example.omg_project.domain.reviewpost.dto.ReviewPostDto;
import com.example.omg_project.domain.reviewpost.service.ReviewPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviewPosts")
@RequiredArgsConstructor
public class ReviewPostApiController {
    private final ReviewPostService reviewPostService;

    /**
     * 후기 게시글 작성
     */
    @PostMapping
    public ResponseEntity<ReviewPostDto.Response> createReviewPost(@RequestBody ReviewPostDto.Request reviewPostRequest) {
        ReviewPostDto.Response reviewPost = reviewPostService.createReviewPost(reviewPostRequest);
        return ResponseEntity.ok(reviewPost);
    }

    /**
     * 후기 게시글 단건 조회
     */
    @GetMapping("/{postId}")
    public ResponseEntity<ReviewPostDto.Response> getReviewPostById(@PathVariable(name = "postId") Long id) {
        ReviewPostDto.Response reviewPostById = reviewPostService.findReviewPostById(id);
        return ResponseEntity.ok(reviewPostById);
    }

    /**
     * 후기 게시글 전체 조회 또는 지역별 조회
     */
    @GetMapping
    public ResponseEntity<List<ReviewPostDto.Response>> getAllReviewPosts(@RequestParam(required = false) String city) {
        List<ReviewPostDto.Response> reviewPosts;
        if (city != null && !city.isEmpty()) {
            reviewPosts = reviewPostService.findReviewPostsByCity(city);
        } else {
            reviewPosts = reviewPostService.findAllReviewPost();
        }
        return ResponseEntity.ok(reviewPosts);
    }

    /**
     * 특정 사용자의 일행 게시글 전체 조회
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewPostDto.Response>> getReviewPostsByUserId(@PathVariable Long userId) {
        List<ReviewPostDto.Response> reviewPosts = reviewPostService.findReviewPostsByUserId(userId);
        return ResponseEntity.ok(reviewPosts);
    }

    /**
     * 게시글 검색
     */
    @GetMapping("/search")
    public ResponseEntity<List<ReviewPostDto.Response>> searchReviewPosts(@RequestParam String searchOption, @RequestParam String keyword) {
        List<ReviewPostDto.Response> searchResults = reviewPostService.searchReviewPosts(searchOption, keyword);
        return ResponseEntity.ok(searchResults);
    }

    /**
     * 후기 게시글 수정
     */
    @PutMapping("/{postId}")
    public ResponseEntity<ReviewPostDto.Response> updateReviewPost(@PathVariable(name = "postId") Long id, @RequestBody ReviewPostDto.Request reviewPostRequest) {
        ReviewPostDto.Response updateReviewPost = reviewPostService.updateReviewPost(id, reviewPostRequest);
        return ResponseEntity.ok(updateReviewPost);
    }

    /**
     * 후기 게시글 삭제
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deleteReviewPostById(@PathVariable(name = "postId") Long id) {
        reviewPostService.deleteReviewPost(id);
        return ResponseEntity.noContent().build();
    }

}
