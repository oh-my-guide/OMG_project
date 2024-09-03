package com.example.omg_project.domain.reviewpost.service.impl;

import com.example.omg_project.domain.reviewpost.dto.PlaceReviewDto;
import com.example.omg_project.domain.reviewpost.repository.PlaceReviewRepository;
import com.example.omg_project.domain.reviewpost.service.PlaceReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceReviewServiceImpl implements PlaceReviewService {
    private final PlaceReviewRepository placeReviewRepository;

    @Override
    public List<PlaceReviewDto.Response> findByReviewPostId(Long reviewPostId) {
        return placeReviewRepository.findByReviewPostId(reviewPostId).stream().map(PlaceReviewDto.Response::fromEntity).collect(Collectors.toList());
    }
}

