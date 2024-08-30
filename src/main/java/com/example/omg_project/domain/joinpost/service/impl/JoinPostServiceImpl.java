package com.example.omg_project.domain.joinpost.service.impl;

import com.example.omg_project.domain.joinpost.dto.JoinPostDto;
import com.example.omg_project.domain.joinpost.entity.JoinPost;
import com.example.omg_project.domain.joinpost.repository.JoinPostRepository;
import com.example.omg_project.domain.joinpost.service.JoinPostService;
import com.example.omg_project.domain.trip.entity.Trip;
import com.example.omg_project.domain.trip.repository.TripRepository;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.repository.UserRepository;
import com.example.omg_project.global.exception.CustomException;
import com.example.omg_project.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JoinPostServiceImpl implements JoinPostService {
    private final JoinPostRepository joinPostRepository;
    private final UserRepository userRepository;
    private final TripRepository tripRepository;

    @Override
    @Transactional
    public JoinPostDto.Response createJoinPost(JoinPostDto.Request joinPostRequest) {
        User user = userRepository.findById(joinPostRequest.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        Trip trip = tripRepository.findById(joinPostRequest.getTripId())
                .orElseThrow(() -> new CustomException(ErrorCode.TRIP_NOT_FOUND_EXCEPTION));

        // JoinPost 엔티티 생성
        JoinPost joinPost = joinPostRequest.toEntity(user, trip);

        // 엔티티 저장
        joinPostRepository.save(joinPost);

        // 저장된 엔티티를 DTO로 변환하여 반환
        return JoinPostDto.Response.fromEntity(joinPost);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JoinPostDto.Response> findAllJoinPost(String sort) {
        Sort sorting = Sort.by(Sort.Direction.DESC, "createdAt"); // 기본 정렬: 최신순
        if ("views".equals(sort)) {
            sorting = Sort.by(Sort.Direction.DESC, "views"); // 인기순 정렬
        }

        return joinPostRepository.findAll(sorting)
                .stream()
                .map(JoinPostDto.Response::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JoinPostDto.Response> findJoinPostsByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_EXCEPTION));
        return joinPostRepository.findJoinPostByUserId(user.getId()).stream().map(JoinPostDto.Response::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JoinPostDto.Response> findJoinPostsByCity(Long cityId, String sort) {
        Sort sorting = Sort.by(Sort.Direction.DESC, "createdAt"); // 기본 정렬: 최신순
        if ("views".equals(sort)) {
            sorting = Sort.by(Sort.Direction.DESC, "views"); // 인기순 정렬
        }

        return joinPostRepository.findByTrip_CityId(cityId, sorting)
                .stream()
                .map(JoinPostDto.Response::fromEntity)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public List<JoinPostDto.Response> searchJoinPosts(String searchOption, String keyword) {
        List<JoinPost> results;

        // 검색 옵션에 따라 조회
        switch (searchOption) {
            case "title":
                results = joinPostRepository.findByTitleContaining(keyword);
                break;
            case "content":
                results = joinPostRepository.findByContentContaining(keyword);
                break;
            case "usernick":
                results = joinPostRepository.findByUser_UsernickContaining(keyword);
                break;
            default:
                throw new CustomException(ErrorCode.INVALID_SEARCH_OPTION_EXCEPTION);
        }

        return results.stream().map(JoinPostDto.Response::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public JoinPostDto.Response findJoinPostById(Long id) {
        JoinPost joinPost = joinPostRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND_EXCEPTION));
        return JoinPostDto.Response.fromEntity(joinPost);
    }

    @Override
    @Transactional
    public JoinPostDto.Response updateJoinPost(Long id, JoinPostDto.Request joinPostRequest) {
        JoinPost originPost = joinPostRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND_EXCEPTION));

        // 엔티티 메서드를 통해 업데이트
        originPost.updateContent(joinPostRequest.getTitle(), joinPostRequest.getContent());

        return JoinPostDto.Response.fromEntity(originPost);
    }

    @Override
    @Transactional
    public void deleteJoinPost(Long id) {
        joinPostRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsJoinPostByTripId(Long tripId) {
        return joinPostRepository.existsJoinPostByTripId(tripId);
    }

    @Override
    @Transactional
    public void incrementViews(Long id) {
        JoinPost joinPost = joinPostRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND_EXCEPTION));
        joinPost.incrementViews();
    }

}
