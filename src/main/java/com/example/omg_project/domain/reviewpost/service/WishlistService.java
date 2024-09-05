package com.example.omg_project.domain.reviewpost.service;

import com.example.omg_project.domain.reviewpost.dto.WishlistDto;

import java.util.List;

public interface WishlistService {
    // 찜하기 토글
    void toggleWishlist(WishlistDto.Request request);
    // 찜하기 상태 확인
    boolean isPostInWishlist(Long userId, Long postId);
    // 찜하기 목록 조회
    List<WishlistDto.Response> getWishlistByUserId(Long userId);
}
