package com.example.omg_project.domain.reviewpost.service;

import com.example.omg_project.domain.reviewpost.dto.PlaceReviewDto;

import java.util.List;

public interface PlaceReviewService {
    List<PlaceReviewDto.Response> findByReviewPostId(Long reviewPostId);
}
