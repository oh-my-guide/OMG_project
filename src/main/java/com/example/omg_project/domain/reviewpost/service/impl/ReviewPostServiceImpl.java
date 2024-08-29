package com.example.omg_project.domain.reviewpost.service.impl;

import com.example.omg_project.domain.reviewpost.dto.PlaceReviewDto;
import com.example.omg_project.domain.reviewpost.dto.ReviewPostDto;
import com.example.omg_project.domain.reviewpost.entity.PlaceReview;
import com.example.omg_project.domain.reviewpost.entity.ReviewPost;
import com.example.omg_project.domain.reviewpost.repository.PlaceReviewRepository;
import com.example.omg_project.domain.reviewpost.repository.ReviewPostRepository;
import com.example.omg_project.domain.reviewpost.service.ReviewPostService;
import com.example.omg_project.domain.trip.entity.Trip;
import com.example.omg_project.domain.trip.entity.TripLocation;
import com.example.omg_project.domain.trip.repository.TripLocationRepository;
import com.example.omg_project.domain.trip.repository.TripRepository;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewPostServiceImpl implements ReviewPostService {
    private final ReviewPostRepository reviewPostRepository;
    private final UserRepository userRepository;
    private final TripRepository tripRepository;
    private final TripLocationRepository tripLocationRepository;
    private final PlaceReviewRepository placeReviewRepository;

    @Override
    @Transactional
    public ReviewPostDto.Response createReviewPost(ReviewPostDto.Request reviewPostRequest) {
        // 1. User 및 Trip을 조회
        User user = userRepository.findById(reviewPostRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Trip trip = tripRepository.findById(reviewPostRequest.getTripId())
                .orElseThrow(() -> new RuntimeException("여행을 찾을 수 없습니다."));

        // 2. ReviewPost 엔티티 생성 및 저장
        ReviewPost reviewPost = reviewPostRequest.toEntity(user, trip);
        reviewPostRepository.save(reviewPost);

        // 3. PlaceReview 엔티티 생성 및 저장
        List<PlaceReview> placeReviews = reviewPostRequest.getReviews().stream().map(placeReviewDto -> {
                    TripLocation tripLocation = tripLocationRepository.findById(placeReviewDto.getTripLocationId())
                            .orElseThrow(() -> new RuntimeException("장소를 찾을 수 없습니다."));
                    return placeReviewDto.toEntity(tripLocation, reviewPost);
                })
                .collect(Collectors.toList());

        placeReviewRepository.saveAll(placeReviews);

        // 4. 저장된 ReviewPost를 DTO로 변환하여 반환
        return ReviewPostDto.Response.fromEntity(reviewPost);
    }

    @Override
    public List<ReviewPostDto.Response> findAllReviewPost() {
        return reviewPostRepository.findAll().stream().map(ReviewPostDto.Response::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewPostDto.Response> findAllReviewPost(String sort) {
        Sort sorting = Sort.by(Sort.Direction.DESC, "createdAt"); // 기본 정렬: 최신순
        if ("views".equals(sort)) {
            sorting = Sort.by(Sort.Direction.DESC, "views"); // 인기순 정렬
        }
        return reviewPostRepository.findAll(sorting).stream().map(ReviewPostDto.Response::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewPostDto.Response> findReviewPostsByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return reviewPostRepository.findReviewPostByUserId(user.getId()).stream().map(ReviewPostDto.Response::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<ReviewPostDto.Response> findReviewPostsByCity(Long cityId, String sort) {
        Sort sorting = Sort.by(Sort.Direction.DESC, "createdAt"); // 기본 정렬: 최신순
        if ("views".equals(sort)) {
            sorting = Sort.by(Sort.Direction.DESC, "views"); // 인기순 정렬
        }
        return reviewPostRepository.findByTrip_CityId(cityId, sorting).stream().map(ReviewPostDto.Response::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<ReviewPostDto.Response> searchReviewPosts(String searchOption, String keyword) {
        List<ReviewPost> results;

        // 검색 옵션에 따라 조회
        switch (searchOption) {
            case "title":
                results = reviewPostRepository.findByTitleContaining(keyword);
                break;
            case "content":
                results = reviewPostRepository.findByContentContaining(keyword);
                break;
            case "usernick":
                results = reviewPostRepository.findByUser_UsernickContaining(keyword);
                break;
            default:
                throw new IllegalArgumentException("검색 옵션이 유효하지 않습니다.");
        }

        return results.stream().map(ReviewPostDto.Response::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewPostDto.Response findReviewPostById(Long id) {
        ReviewPost reviewPost = reviewPostRepository.findWithReviewsById(id).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        return ReviewPostDto.Response.fromEntity(reviewPost);
    }

    @Override
    @Transactional
    public ReviewPostDto.Response updateReviewPost(Long id, ReviewPostDto.Request reviewPostRequest) {
        // 1. 기존 게시글 조회
        ReviewPost originPost = reviewPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 2. 게시글 제목 및 내용 업데이트
        originPost.updateContent(reviewPostRequest.getTitle(), reviewPostRequest.getContent());

        // 3. 기존 리뷰 삭제 - 사용자의 모든 시나리오를 처리하기 위해 기존 리뷰 삭제 후 저장하기로 함.
        placeReviewRepository.deleteByReviewPostId(id);

        // 4. 새로운 리뷰 데이터를 엔티티로 변환하여 저장
        List<PlaceReview> placeReviews = reviewPostRequest.getReviews().stream()
                .map(placeReviewDto -> {
                    TripLocation tripLocation = tripLocationRepository.findById(placeReviewDto.getTripLocationId())
                            .orElseThrow(() -> new RuntimeException("장소를 찾을 수 없습니다."));
                    return placeReviewDto.toEntity(tripLocation, originPost);
                })
                .collect(Collectors.toList());

        placeReviewRepository.saveAll(placeReviews);

        // 5. 업데이트된 게시글을 DTO로 변환하여 반환
        return ReviewPostDto.Response.fromEntity(originPost);
    }

    @Override
    @Transactional
    public void deleteReviewPost(Long id) {
        reviewPostRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void incrementViews(Long id) {
        ReviewPost reviewPost = reviewPostRepository.findById(id).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        reviewPost.incrementViews();
    }
}
