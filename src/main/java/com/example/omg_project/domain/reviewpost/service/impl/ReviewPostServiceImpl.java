package com.example.omg_project.domain.reviewpost.service.impl;

import com.example.omg_project.domain.reviewpost.dto.ReviewPostDto;
import com.example.omg_project.domain.reviewpost.entity.ReviewPost;
import com.example.omg_project.domain.reviewpost.repository.ReviewPostRepository;
import com.example.omg_project.domain.reviewpost.service.ReviewPostService;
import com.example.omg_project.domain.trip.entity.Trip;
import com.example.omg_project.domain.trip.repository.TripRepository;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewPostServiceImpl implements ReviewPostService {
    private final ReviewPostRepository reviewPostRepository;
    private final UserRepository userRepository;
    private final TripRepository tripRepository;

    @Override
    @Transactional
    public ReviewPostDto.Response createReviewPost(ReviewPostDto.Request reviewPostRequest) {
        User user = userRepository.findById(reviewPostRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Trip trip = tripRepository.findById(reviewPostRequest.getTripId())
                .orElseThrow(() -> new RuntimeException("여행을 찾을 수 없습니다."));

        // ReviewPost 엔티티 생성
        ReviewPost reviewPost = reviewPostRequest.toEntity(user, trip);

        // 엔티티 저장
        reviewPostRepository.save(reviewPost);

        // 저장된 엔티티를 DTO로 변환하여 반환
        return ReviewPostDto.Response.fromEntity(reviewPost);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewPostDto.Response> findAllReviewPost() {
        return reviewPostRepository.findAll().stream().map(ReviewPostDto.Response::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewPostDto.Response> findReviewPostsByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return reviewPostRepository.findReviewPostByUserId(user.getId()).stream().map(ReviewPostDto.Response::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewPostDto.Response findReviewPostById(Long id) {
        ReviewPost reviewPost = reviewPostRepository.findById(id).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        return ReviewPostDto.Response.fromEntity(reviewPost);
    }

    @Override
    @Transactional
    public ReviewPostDto.Response updateReviewPost(Long id, ReviewPostDto.Request reviewPostRequest) {
        ReviewPost originPost = reviewPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 엔티티 메서드를 통해 업데이트
        originPost.updateContent(reviewPostRequest.getTitle(), reviewPostRequest.getContent());

        return ReviewPostDto.Response.fromEntity(originPost);
    }

    @Override
    @Transactional
    public void deleteReviewPost(Long id) {
        reviewPostRepository.deleteById(id);
    }
}
