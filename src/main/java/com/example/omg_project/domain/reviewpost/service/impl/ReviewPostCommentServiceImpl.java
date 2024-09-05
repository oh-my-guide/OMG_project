package com.example.omg_project.domain.reviewpost.service.impl;

import com.example.omg_project.domain.joinpost.entity.JoinPostComment;
import com.example.omg_project.domain.notification.service.NotificationService;
import com.example.omg_project.domain.reviewpost.dto.ReviewPostCommentDto;
import com.example.omg_project.domain.reviewpost.entity.ReviewPost;
import com.example.omg_project.domain.reviewpost.entity.ReviewPostComment;
import com.example.omg_project.domain.reviewpost.repository.ReviewPostCommentRepository;
import com.example.omg_project.domain.reviewpost.repository.ReviewPostRepository;
import com.example.omg_project.domain.reviewpost.service.ReviewPostCommentService;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.repository.UserRepository;
import com.example.omg_project.global.exception.CustomException;
import com.example.omg_project.global.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    private final NotificationService notificationService;

    /**
     * 댓글 작성
     */
    @Override
    @Transactional
    public ReviewPostCommentDto.Response createComment(Long postId, Long userId, ReviewPostCommentDto.Request commentRequest) throws JsonProcessingException {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_EXCEPTION));
        ReviewPost reviewPost = reviewPostRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND_EXCEPTION));
        // 댓글 엔티티 생성
        ReviewPostComment reviewPostComment = commentRequest.toEntity(user, reviewPost);

        User postUser = reviewPostRepository.findById(postId).get().getUser();

        // 댓글 저장
        reviewPostCommentRepository.save(reviewPostComment);
        notificationService.createNotification(postUser, user.getUsernick() + ": " + commentRequest.getContent(), "REVIEWPOSTCOMMENT", reviewPostComment.getId());


        // 저장된 댓글 엔티티를 DTO로 변환하여 반환
        return ReviewPostCommentDto.Response.fromEntity(reviewPostComment);
    }

    /**
     * 댓글 전체 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReviewPostCommentDto.Response> findAllByPostId(Long postId) {
        return reviewPostCommentRepository.findAllByReviewPostId(postId).stream()
                .map(ReviewPostCommentDto.Response::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 댓글 수정
     */
    @Override
    @Transactional
    public ReviewPostCommentDto.Response updateComment(Long commentId, ReviewPostCommentDto.Request commentRequest) {
        ReviewPostComment reviewPostComment = reviewPostCommentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION));

        // 엔티티 메서드를 통해 업데이트
        reviewPostComment.updateContent(commentRequest.getContent());

        return ReviewPostCommentDto.Response.fromEntity(reviewPostComment);
    }

    /**
     * 댓글 삭제
     */
    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        reviewPostCommentRepository.deleteById(commentId);
    }

    /**
     * 댓글 전체 삭제
     */
    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        List<ReviewPostComment> reviewPostComments = reviewPostCommentRepository.findByUserId(userId);
        reviewPostCommentRepository.deleteAll(reviewPostComments);
    }
}
