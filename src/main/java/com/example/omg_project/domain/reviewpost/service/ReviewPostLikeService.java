package com.example.omg_project.domain.reviewpost.service;

import java.util.Map;

public interface ReviewPostLikeService {
    // 좋아요 상태 확인
    Map<String, Object> getLikeInfo(Long userId, Long postId);
    // 좋아요 토글
    void toggleLike(Long userId, Long postId);
}
