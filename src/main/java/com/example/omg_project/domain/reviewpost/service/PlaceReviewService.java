package com.example.omg_project.domain.reviewpost.service;

import com.example.omg_project.domain.reviewpost.dto.PlaceReviewDto;

import java.util.List;

public interface PlaceReviewService {
    // 장소별 후기 전체 조회
    List<PlaceReviewDto.Response> findByReviewPostId(Long reviewPostId);
}
