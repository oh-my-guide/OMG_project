package com.example.omg_project.domain.trip.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TourApiController {

    @Value("${tour.api.key}")
    private String apiKey;

    @GetMapping("/api/key")
    public String getApiKey() {
        return apiKey;
    }
}