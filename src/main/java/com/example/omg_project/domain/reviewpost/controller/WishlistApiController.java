package com.example.omg_project.domain.reviewpost.controller;

import com.example.omg_project.domain.reviewpost.dto.WishlistDto;
import com.example.omg_project.domain.reviewpost.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviewPosts/wishlists")
@RequiredArgsConstructor
public class WishlistApiController {
    private final WishlistService wishlistService;

    /**
     * 찜하기 토글
     */
    @PostMapping
    public ResponseEntity<Void> toggleWishlist(@RequestBody WishlistDto.Request request) {
        wishlistService.toggleWishlist(request);
        return ResponseEntity.ok().build();
    }

    /**
     * 찜하기 상태 확인
     */
    @GetMapping
    public ResponseEntity<Boolean> isPostInWishlist(@RequestParam Long userId, @RequestParam Long postId) {
        boolean isInWishlist = wishlistService.isPostInWishlist(userId, postId);
        return ResponseEntity.ok(isInWishlist);
    }

    /**
     * 찜한 게시글 전체 조회
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<WishlistDto.Response>> getWishlistByUser(@PathVariable Long userId) {
        List<WishlistDto.Response> wishlistByUserId = wishlistService.getWishlistByUserId(userId);
        return ResponseEntity.ok(wishlistByUserId);
    }

}
