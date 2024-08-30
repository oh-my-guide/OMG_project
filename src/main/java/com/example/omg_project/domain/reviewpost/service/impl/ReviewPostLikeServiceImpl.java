package com.example.omg_project.domain.reviewpost.service.impl;

import com.example.omg_project.domain.reviewpost.entity.ReviewPost;
import com.example.omg_project.domain.reviewpost.entity.ReviewPostLike;
import com.example.omg_project.domain.reviewpost.repository.ReviewPostLikeRepository;
import com.example.omg_project.domain.reviewpost.repository.ReviewPostRepository;
import com.example.omg_project.domain.reviewpost.service.ReviewPostLikeService;
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
public class ReviewPostLikeServiceImpl implements ReviewPostLikeService {
    private final ReviewPostLikeRepository reviewPostLikeRepository;
    private final UserRepository userRepository;
    private final ReviewPostRepository reviewPostRepository;

    @Override
    public Map<String, Object> getLikeInfo(Long userId, Long postId) {
        // 좋아요 여부 확인
        boolean liked = reviewPostLikeRepository.existsByUserIdAndReviewPostId(userId, postId);

        // 좋아요 수 계산
        int likeCount = reviewPostLikeRepository.countByReviewPostId(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("liked", liked);
        response.put("likeCount", likeCount);

        return response;
    }

    @Override
    public void toggleLike(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        ReviewPost reviewPost = reviewPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND_EXCEPTION));

        // 좋아요 상태 확인
        ReviewPostLike existingLike = reviewPostLikeRepository.findByUserAndReviewPost(user, reviewPost);

        if (existingLike != null) {
            // 좋아요가 이미 있는 경우, 삭제 (좋아요 취소)
            reviewPostLikeRepository.delete(existingLike);
        } else {
            // 좋아요가 없는 경우, 새 좋아요 추가
            ReviewPostLike newLike = ReviewPostLike.builder()
                    .user(user)
                    .reviewPost(reviewPost)
                    .build();
            reviewPostLikeRepository.save(newLike);
        }
    }
}