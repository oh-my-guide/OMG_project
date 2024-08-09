package com.example.omg_project.domain.reviewpost.service;

import java.util.Map;

public interface ReviewPostLikeService {
    Map<String, Object> getLikeInfo(Long userId, Long postId);
    void toggleLike(Long userId, Long postId);
}
