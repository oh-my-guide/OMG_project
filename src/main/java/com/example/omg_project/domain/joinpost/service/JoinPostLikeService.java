package com.example.omg_project.domain.joinpost.service;

import java.util.Map;

public interface JoinPostLikeService {
    // 좋아요 상태 조회
    Map<String, Object> getLikeInfo(Long userId, Long postId);
    // 좋아요 토글
    void toggleLike(Long userId, Long postId);
}
