package com.example.omg_project.domain.reviewpost.service;

import com.example.omg_project.domain.reviewpost.dto.WishlistDto;

import java.util.List;

public interface WishlistService {
    void toggleWishlist(WishlistDto.Request request);
    boolean isPostInWishlist(Long userId, Long postId);
    List<WishlistDto.Response> getWishlistByUserId(Long userId);
}
