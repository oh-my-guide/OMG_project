package com.example.omg_project.domain.joinpost.service;

import java.util.Map;

public interface JoinPostLikeService {

    Map<String, Object> getLikeInfo(Long userId, Long postId);

    void toggleLike(Long userId, Long postId);
}
