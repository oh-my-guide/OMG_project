package com.example.omg_project.domain.joinpost.service.impl;

import com.example.omg_project.domain.joinpost.dto.JoinPostCommentDto;
import com.example.omg_project.domain.joinpost.entity.JoinPost;
import com.example.omg_project.domain.joinpost.entity.JoinPostComment;
import com.example.omg_project.domain.joinpost.repository.JoinPostCommentRepository;
import com.example.omg_project.domain.joinpost.repository.JoinPostRepository;
import com.example.omg_project.domain.joinpost.service.JoinPostCommentService;
import com.example.omg_project.domain.reviewpost.entity.ReviewPostComment;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.repository.UserRepository;
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

    @Override
    @Transactional
    public JoinPostCommentDto.Response createComment(Long postId, Long userId, JoinPostCommentDto.Request commentRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        JoinPost joinPost = joinPostRepository.findById(postId).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        // 댓글 엔티티 생성
        JoinPostComment joinPostComment = commentRequest.toEntity(user, joinPost);

        // 댓글 저장
        joinPostCommentRepository.save(joinPostComment);

        // 저장된 댓글 엔티티를 DTO로 변환하여 반환
        return JoinPostCommentDto.Response.fromEntity(joinPostComment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JoinPostCommentDto.Response> findAllByPostId(Long postId) {
        return joinPostCommentRepository.findAllByJoinPostId(postId).stream()
                .map(JoinPostCommentDto.Response::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public JoinPostCommentDto.Response updateComment(Long commentId, JoinPostCommentDto.Request commentRequest) {
        JoinPostComment joinPostComment = joinPostCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        // 엔티티 메서드를 통해 업데이트
        joinPostComment.updateContent(commentRequest.getContent());

        return JoinPostCommentDto.Response.fromEntity(joinPostComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        joinPostCommentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        List<JoinPostComment> joinPostComments = joinPostCommentRepository.findByUserId(userId);
        joinPostCommentRepository.deleteAll(joinPostComments);
    }
}
