package com.example.omg_project.domain.joinpost.service.impl;

import com.example.omg_project.domain.joinpost.dto.JoinPostDto;
import com.example.omg_project.domain.joinpost.entity.JoinPost;
import com.example.omg_project.domain.joinpost.repository.JoinPostRepository;
import com.example.omg_project.domain.joinpost.service.JoinPostService;
import com.example.omg_project.domain.trip.entity.Trip;
import com.example.omg_project.domain.trip.service.TripService;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JoinPostServiceImpl implements JoinPostService {
    private final JoinPostRepository joinPostRepository;

    @Override
    @Transactional
    public JoinPostDto.Response createJoinPost(JoinPostDto.Request joinPostRequest, Long userId, Long tripId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
//
//        Trip trip = tripRepository.findById(tripId)
//                .orElseThrow(() -> new RuntimeException("여행을 찾을 수 없습니다."));

        // JoinPost 엔티티 생성
//        JoinPost joinPost = joinPostRequest.toEntity(user, trip);

        // 엔티티 저장
//        joinPostRepository.save(joinPost);

        // 저장된 엔티티를 DTO로 변환하여 반환
//        return JoinPostDto.Response.fromEntity(joinPost);
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<JoinPostDto.Response> findAllJoinPost() {
        return joinPostRepository.findAll().stream().map(JoinPostDto.Response::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public JoinPostDto.Response findJoinPostById(Long id) {
        JoinPost joinPost = joinPostRepository.findById(id).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        return JoinPostDto.Response.fromEntity(joinPost);
    }

    @Override
    @Transactional
    public JoinPostDto.Response updateJoinPost(Long id, JoinPostDto.Request joinPostRequest) {
        JoinPost originPost = joinPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 엔티티 메서드를 통해 업데이트
        originPost.updateContent(joinPostRequest.getTitle(), joinPostRequest.getContent());

        return JoinPostDto.Response.fromEntity(originPost);
    }

    @Override
    @Transactional
    public void deleteJoinPost(Long id) {
        joinPostRepository.deleteById(id);
    }



}
