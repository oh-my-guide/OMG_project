package com.example.omg_project.domain.joinpost.service.impl;

import com.example.omg_project.domain.joinpost.entity.JoinPost;
import com.example.omg_project.domain.joinpost.entity.JoinPostLike;
import com.example.omg_project.domain.joinpost.repository.JoinPostLikeRepository;
import com.example.omg_project.domain.joinpost.repository.JoinPostRepository;
import com.example.omg_project.domain.joinpost.service.JoinPostLikeService;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JoinPostLikeServiceImpl implements JoinPostLikeService {
    private final JoinPostLikeRepository joinPostLikeRepository;
    private final UserRepository userRepository;
    private final JoinPostRepository joinPostRepository;

    @Override
    public Map<String, Object> getLikeInfo(Long userId, Long postId) {
        // 좋아요 여부 확인
        boolean liked = joinPostLikeRepository.existsByUserIdAndJoinPostId(userId, postId);

        // 좋아요 수 계산
        int likeCount = joinPostLikeRepository.countByJoinPostId(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("liked", liked);
        response.put("likeCount", likeCount);

        return response;
    }

    @Override
    public void toggleLike(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        JoinPost joinPost = joinPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시물을 찾을 수 없습니다."));

        // 좋아요 상태 확인
        JoinPostLike existingLike = joinPostLikeRepository.findByUserAndJoinPost(user, joinPost);

        if (existingLike != null) {
            // 좋아요가 이미 있는 경우, 삭제 (좋아요 취소)
            joinPostLikeRepository.delete(existingLike);
        } else {
            // 좋아요가 없는 경우, 새 좋아요 추가
            JoinPostLike newLike = JoinPostLike.builder()
                    .user(user)
                    .joinPost(joinPost)
                    .build();
            joinPostLikeRepository.save(newLike);
        }
    }
}