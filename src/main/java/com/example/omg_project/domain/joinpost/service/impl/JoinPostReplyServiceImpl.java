package com.example.omg_project.domain.joinpost.service.impl;

import com.example.omg_project.domain.joinpost.dto.JoinPostReplyDto;
import com.example.omg_project.domain.joinpost.entity.JoinPostComment;
import com.example.omg_project.domain.joinpost.entity.JoinPostReply;
import com.example.omg_project.domain.joinpost.repository.JoinPostCommentRepository;
import com.example.omg_project.domain.joinpost.repository.JoinPostReplyRepository;
import com.example.omg_project.domain.joinpost.service.JoinPostReplyService;
import com.example.omg_project.domain.notification.service.NotificationService;
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
public class JoinPostReplyServiceImpl implements JoinPostReplyService {
    private final JoinPostReplyRepository joinPostReplyRepository;
    private final UserRepository userRepository;
    private final JoinPostCommentRepository joinPostCommentRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public JoinPostReplyDto.Response createReply(Long commentId, Long userId, JoinPostReplyDto.Request replyRequest) throws JsonProcessingException {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_EXCEPTION));
        JoinPostComment joinPostComment = joinPostCommentRepository.findById(commentId).orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION));
        // 대댓글 엔티티 생성
        JoinPostReply joinPostReply = replyRequest.toEntity(user, joinPostComment);
        User commentUser = joinPostCommentRepository.findById(commentId).get().getUser();

        // 대댓글 저장
        joinPostReplyRepository.save(joinPostReply);
        notificationService.createNotification(commentUser, user.getUsernick() + ": " + replyRequest.getContent(), "JOINPOSTREPLY", joinPostReply.getId());


        // 저장된 대댓글 엔티티를 DTO로 변환하여 반환
        return JoinPostReplyDto.Response.fromEntity(joinPostReply);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JoinPostReplyDto.Response> findAllByCommentId(Long commentId) {
        return joinPostReplyRepository.findAllByJoinPostCommentId(commentId).stream()
                .map(JoinPostReplyDto.Response::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public JoinPostReplyDto.Response updateReply(Long replyId, JoinPostReplyDto.Request replyRequest) {
        JoinPostReply joinPostReply = joinPostReplyRepository.findById(replyId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPLY_NOT_FOUND_EXCEPTION));

        // 엔티티 메서드를 통해 업데이트
        joinPostReply.updateContent(replyRequest.getContent());

        return JoinPostReplyDto.Response.fromEntity(joinPostReply);
    }

    @Override
    @Transactional
    public void deleteReply(Long replyId) {
        joinPostReplyRepository.deleteById(replyId);
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        List<JoinPostReply> joinPostReplies = joinPostReplyRepository.findByUserId(userId);
        joinPostReplyRepository.deleteAll(joinPostReplies);
    }
}
