package com.example.omg_project.domain.reviewpost.service.impl;

import com.example.omg_project.domain.reviewpost.dto.ReviewPostReplyDto;
import com.example.omg_project.domain.reviewpost.entity.ReviewPostComment;
import com.example.omg_project.domain.reviewpost.entity.ReviewPostReply;
import com.example.omg_project.domain.reviewpost.repository.ReviewPostCommentRepository;
import com.example.omg_project.domain.reviewpost.repository.ReviewPostReplyRepository;
import com.example.omg_project.domain.reviewpost.service.ReviewPostReplyService;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.repository.UserRepository;
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

    @Override
    @Transactional
    public ReviewPostReplyDto.Response createReply(Long commentId, Long userId, ReviewPostReplyDto.Request replyRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        ReviewPostComment reviewPostComment = reviewPostCommentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        // 대댓글 엔티티 생성
        ReviewPostReply reviewPostReply = replyRequest.toEntity(user, reviewPostComment);

        // 대댓글 저장
        reviewPostReplyRepository.save(reviewPostReply);

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
                .orElseThrow(() -> new RuntimeException("대댓글을 찾을 수 없습니다."));

        // 엔티티 메서드를 통해 업데이트
        reviewPostReply.updateContent(replyRequest.getContent());

        return ReviewPostReplyDto.Response.fromEntity(reviewPostReply);
    }

    @Override
    @Transactional
    public void deleteReply(Long replyId) {
        reviewPostReplyRepository.deleteById(replyId);
    }
}
