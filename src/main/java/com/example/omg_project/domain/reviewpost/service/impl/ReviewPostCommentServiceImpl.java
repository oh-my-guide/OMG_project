package com.example.omg_project.domain.reviewpost.service.impl;

import com.example.omg_project.domain.joinpost.entity.JoinPostComment;
import com.example.omg_project.domain.reviewpost.dto.ReviewPostCommentDto;
import com.example.omg_project.domain.reviewpost.entity.ReviewPost;
import com.example.omg_project.domain.reviewpost.entity.ReviewPostComment;
import com.example.omg_project.domain.reviewpost.repository.ReviewPostCommentRepository;
import com.example.omg_project.domain.reviewpost.repository.ReviewPostRepository;
import com.example.omg_project.domain.reviewpost.service.ReviewPostCommentService;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewPostCommentServiceImpl implements ReviewPostCommentService {
    private final ReviewPostCommentRepository reviewPostCommentRepository;
    private final UserRepository userRepository;
    private final ReviewPostRepository reviewPostRepository;

    @Override
    @Transactional
    public ReviewPostCommentDto.Response createComment(Long postId, Long userId, ReviewPostCommentDto.Request commentRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        ReviewPost reviewPost = reviewPostRepository.findById(postId).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        // 댓글 엔티티 생성
        ReviewPostComment reviewPostComment = commentRequest.toEntity(user, reviewPost);

        // 댓글 저장
        reviewPostCommentRepository.save(reviewPostComment);

        // 저장된 댓글 엔티티를 DTO로 변환하여 반환
        return ReviewPostCommentDto.Response.fromEntity(reviewPostComment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewPostCommentDto.Response> findAllByPostId(Long postId) {
        return reviewPostCommentRepository.findAllByReviewPostId(postId).stream()
                .map(ReviewPostCommentDto.Response::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReviewPostCommentDto.Response updateComment(Long commentId, ReviewPostCommentDto.Request commentRequest) {
        ReviewPostComment reviewPostComment = reviewPostCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        // 엔티티 메서드를 통해 업데이트
        reviewPostComment.updateContent(commentRequest.getContent());

        return ReviewPostCommentDto.Response.fromEntity(reviewPostComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        reviewPostCommentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        List<ReviewPostComment> reviewPostComments = reviewPostCommentRepository.findByUserId(userId);
        reviewPostCommentRepository.deleteAll(reviewPostComments);
    }
}
