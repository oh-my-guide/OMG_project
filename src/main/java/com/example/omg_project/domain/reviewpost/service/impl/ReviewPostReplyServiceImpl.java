package com.example.omg_project.domain.reviewpost.service.impl;

import com.example.omg_project.domain.joinpost.entity.JoinPostReply;
import com.example.omg_project.domain.notification.service.NotificationService;
import com.example.omg_project.domain.reviewpost.dto.ReviewPostReplyDto;
import com.example.omg_project.domain.reviewpost.entity.ReviewPostComment;
import com.example.omg_project.domain.reviewpost.entity.ReviewPostReply;
import com.example.omg_project.domain.reviewpost.repository.ReviewPostCommentRepository;
import com.example.omg_project.domain.reviewpost.repository.ReviewPostReplyRepository;
import com.example.omg_project.domain.reviewpost.service.ReviewPostReplyService;
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
public class ReviewPostReplyServiceImpl implements ReviewPostReplyService {
    private final ReviewPostReplyRepository reviewPostReplyRepository;
    private final UserRepository userRepository;
    private final ReviewPostCommentRepository reviewPostCommentRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public ReviewPostReplyDto.Response createReply(Long commentId, Long userId, ReviewPostReplyDto.Request replyRequest) throws JsonProcessingException {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_EXCEPTION));
        ReviewPostComment reviewPostComment = reviewPostCommentRepository.findById(commentId).orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION));
        // 대댓글 엔티티 생성
        ReviewPostReply reviewPostReply = replyRequest.toEntity(user, reviewPostComment);
        User commentUser = reviewPostCommentRepository.findById(commentId).get().getUser();

        // 대댓글 저장
        reviewPostReplyRepository.save(reviewPostReply);
        notificationService.createNotification(commentUser, user.getUsernick() + ": " + replyRequest.getContent(), "REVIEWPOSTREPLY", reviewPostReply.getId());


        // 저장된 대댓글 엔티티를 DTO로 변환하여 반환
        return ReviewPostReplyDto.Response.fromEntity(reviewPostReply);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewPostReplyDto.Response> findAllByCommentId(Long commentId) {
        return reviewPostReplyRepository.findAllByReviewPostCommentId(commentId).stream()
                .map(ReviewPostReplyDto.Response::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReviewPostReplyDto.Response updateReply(Long replyId, ReviewPostReplyDto.Request replyRequest) {
        ReviewPostReply reviewPostReply = reviewPostReplyRepository.findById(replyId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPLY_NOT_FOUND_EXCEPTION));

        // 엔티티 메서드를 통해 업데이트
        reviewPostReply.updateContent(replyRequest.getContent());

        return ReviewPostReplyDto.Response.fromEntity(reviewPostReply);
    }

    @Override
    @Transactional
    public void deleteReply(Long replyId) {
        reviewPostReplyRepository.deleteById(replyId);
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        List<ReviewPostReply> reviewPostReplies = reviewPostReplyRepository.findByUserId(userId);
        reviewPostReplyRepository.deleteAll(reviewPostReplies);
    }
}
