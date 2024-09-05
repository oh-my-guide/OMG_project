package com.example.omg_project.domain.reviewpost.repository;

import com.example.omg_project.domain.reviewpost.entity.PlaceReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceReviewRepository extends JpaRepository<PlaceReview, Long> {
    // 장소별 후기 전체 조회
    List<PlaceReview> findByReviewPostId(Long reviewPostId);
    // 장소별 후기 삭제
    void deleteByReviewPostId(Long reviewPostId);
}
