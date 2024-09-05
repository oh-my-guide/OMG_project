package com.example.omg_project.domain.joinpost.controller;

import com.example.omg_project.domain.joinpost.service.JoinPostLikeService;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/joinPosts")
@RequiredArgsConstructor
public class JoinPostLikeApiController {

    private final JoinPostLikeService joinPostLikeService;
    private final UserService userService;

    /**
     * 좋아요 상태 조회
     */
    @GetMapping("/{joinPostId}/likes")
    public ResponseEntity<Map<String, Object>> getLikeInfo (@PathVariable Long joinPostId, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        Map<String, Object> likeInfo = joinPostLikeService.getLikeInfo(user.getId(), joinPostId);
        return ResponseEntity.ok(likeInfo);
    }

    /**
     * 좋아요 토글
     */
    @PostMapping("/{joinPostId}/likes")
    public ResponseEntity<Void> toggleLike(@PathVariable Long joinPostId, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        joinPostLikeService.toggleLike(user.getId(), joinPostId);
        return ResponseEntity.ok().build();
    }
}