package com.example.omg_project.domain.joinpost.service.impl;

import com.example.omg_project.domain.joinpost.entity.JoinPost;
import com.example.omg_project.domain.joinpost.entity.JoinPostLike;
import com.example.omg_project.domain.joinpost.repository.JoinPostLikeRepository;
import com.example.omg_project.domain.joinpost.repository.JoinPostRepository;
import com.example.omg_project.domain.joinpost.service.JoinPostLikeService;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.repository.UserRepository;
import com.example.omg_project.global.exception.CustomException;
import com.example.omg_project.global.exception.ErrorCode;
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

    /**
     * 좋아요 상태 조회
     */
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

    /**
     * 좋아요 토글
     */
    @Override
    public void toggleLike(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        JoinPost joinPost = joinPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND_EXCEPTION));

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