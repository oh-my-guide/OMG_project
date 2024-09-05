package com.example.omg_project.domain.joinpost.service.impl;

import com.example.omg_project.domain.joinpost.dto.JoinPostCommentDto;
import com.example.omg_project.domain.joinpost.entity.JoinPost;
import com.example.omg_project.domain.joinpost.entity.JoinPostComment;
import com.example.omg_project.domain.joinpost.repository.JoinPostCommentRepository;
import com.example.omg_project.domain.joinpost.repository.JoinPostRepository;
import com.example.omg_project.domain.joinpost.service.JoinPostCommentService;
import com.example.omg_project.domain.notification.service.NotificationService;
import com.example.omg_project.domain.reviewpost.entity.ReviewPostComment;
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
public class JoinPostCommentServiceImpl implements JoinPostCommentService {
    private final JoinPostCommentRepository joinPostCommentRepository;
    private final UserRepository userRepository;
    private final JoinPostRepository joinPostRepository;
    private final NotificationService notificationService;

    /**
     * 댓글 작성
     */
    @Override
    @Transactional
    public JoinPostCommentDto.Response createComment(Long postId, Long userId, JoinPostCommentDto.Request commentRequest) throws JsonProcessingException {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_EXCEPTION));
        JoinPost joinPost = joinPostRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND_EXCEPTION));
        // 댓글 엔티티 생성
        JoinPostComment joinPostComment = commentRequest.toEntity(user, joinPost);

        User postUser = joinPostRepository.findById(postId).get().getUser();

        // 댓글 저장
        joinPostCommentRepository.save(joinPostComment);
        notificationService.createNotification(postUser, user.getUsernick() + ": " + commentRequest.getContent(), "JOINPOSTCOMMENT", joinPostComment.getId());


        // 저장된 댓글 엔티티를 DTO로 변환하여 반환
        return JoinPostCommentDto.Response.fromEntity(joinPostComment);
    }

    /**
     * 특정 게시글의 모든 댓글 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<JoinPostCommentDto.Response> findAllByPostId(Long postId) {
        return joinPostCommentRepository.findAllByJoinPostId(postId).stream()
                .map(JoinPostCommentDto.Response::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 댓글 수정
     */
    @Override
    @Transactional
    public JoinPostCommentDto.Response updateComment(Long commentId, JoinPostCommentDto.Request commentRequest) {
        JoinPostComment joinPostComment = joinPostCommentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION));

        // 엔티티 메서드를 통해 업데이트
        joinPostComment.updateContent(commentRequest.getContent());

        return JoinPostCommentDto.Response.fromEntity(joinPostComment);
    }

    /**
     * 댓글 삭제
     */
    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        joinPostCommentRepository.deleteById(commentId);
    }

    /**
     * 모든 댓글 삭제
     */
    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        List<JoinPostComment> joinPostComments = joinPostCommentRepository.findByUserId(userId);
        joinPostCommentRepository.deleteAll(joinPostComments);
    }
}
