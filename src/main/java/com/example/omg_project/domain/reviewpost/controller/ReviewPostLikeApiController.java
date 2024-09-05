package com.example.omg_project.domain.reviewpost.controller;

import com.example.omg_project.domain.reviewpost.service.ReviewPostLikeService;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reviewPosts")
@RequiredArgsConstructor
public class ReviewPostLikeApiController {

    private final ReviewPostLikeService reviewPostLikeService;
    private final UserService userService;

    /**
     * 좋아요 상태 조회
     */
    @GetMapping("/{reviewPostId}/likes")
    public ResponseEntity<Map<String, Object>> getLikeInfo (@PathVariable Long reviewPostId, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        Map<String, Object> likeInfo = reviewPostLikeService.getLikeInfo(user.getId(), reviewPostId);
        return ResponseEntity.ok(likeInfo);
    }

    /**
     * 좋아요 토글
     */
    @PostMapping("/{reviewPostId}/likes")
    public ResponseEntity<Void> toggleLike(@PathVariable Long reviewPostId, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        reviewPostLikeService.toggleLike(user.getId(), reviewPostId);
        return ResponseEntity.ok().build();
    }
}