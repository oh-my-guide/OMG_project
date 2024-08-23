package com.example.omg_project.domain.reviewpost.repository;

import com.example.omg_project.domain.reviewpost.entity.ReviewPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewPostRepository extends JpaRepository<ReviewPost, Long> {
    // 특정 사용자의 전체 게시글 확인
    List<ReviewPost> findReviewPostByUserId(Long userId);
    // 지역별 전체 게시글 조회
    List<ReviewPost> findByTrip_CityName(String cityName);
}
