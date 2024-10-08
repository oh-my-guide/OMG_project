package com.example.omg_project.domain.reviewpost.repository;

import com.example.omg_project.domain.reviewpost.entity.ReviewPost;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewPostRepository extends JpaRepository<ReviewPost, Long> {
    // 장소별 리뷰도 함께 조회
    @EntityGraph(attributePaths = {"placeReviews"})
    Optional<ReviewPost> findWithReviewsById(Long id);
    // 특정 사용자의 전체 게시글 확인
    List<ReviewPost> findReviewPostByUserId(Long userId);
    // 지역별 전체 게시글 조회
    List<ReviewPost> findByTrip_CityId(Long cityId, Sort sort);
    // 검색 기능 추가: 개별 필드별 검색
    List<ReviewPost> findByTitleContaining(String title);
    List<ReviewPost> findByContentContaining(String content);
    List<ReviewPost> findByUser_UsernickContaining(String usernick);
}
