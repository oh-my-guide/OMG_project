package com.example.omg_project.domain.reviewpost.service;

import com.example.omg_project.domain.reviewpost.dto.ReviewPostDto;

import java.util.List;

public interface ReviewPostService {
    // 후기 게시글 작성
    ReviewPostDto.Response createReviewPost(ReviewPostDto.Request reviewPostRequest);
    // 후기 게시글 전체 조회
    List<ReviewPostDto.Response> findAllReviewPost();
    // 특정 사용자의 게시글 전체 조회
    List<ReviewPostDto.Response> findReviewPostsByUserId(Long userId);
    // 지역별 게시글 전체 조회
    List<ReviewPostDto.Response> findReviewPostsByCity(String city);
    // 검색 옵션에 따른 게시글 전체 조회
    List<ReviewPostDto.Response> searchReviewPosts(String searchOption, String keyword);
    // 후기 게시글 상세 조회
    ReviewPostDto.Response findReviewPostById(Long id);
    // 후기 게시글 수정
    ReviewPostDto.Response updateReviewPost(Long id, ReviewPostDto.Request reviewPostRequest);
    // 후기 게시글 삭제
    void deleteReviewPost(Long id);
}
