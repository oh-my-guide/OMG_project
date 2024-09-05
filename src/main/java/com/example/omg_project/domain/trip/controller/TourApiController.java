package com.example.omg_project.domain.trip.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TourApiController {

    /**
     * yml 파일에서 Tour API 키 주입
     */
    @Value("${tour.api.key}")
    private String apiKey;

    /**
     * API 키 반환 엔드포인트
     */
    @GetMapping("/api/key")
    public String getApiKey() {
        return apiKey;
    }
}