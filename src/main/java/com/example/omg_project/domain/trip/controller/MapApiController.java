package com.example.omg_project.domain.trip.controller;

import com.example.omg_project.domain.trip.dto.TripLocationDto;
import com.example.omg_project.domain.trip.service.TripLocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MapApiController {

    private final TripLocationService tripLocationService;

    @PostMapping("/api/save-location")
    public ResponseEntity<String> saveLocation(@RequestBody TripLocationDto tripLocationDto) {
        if (tripLocationDto.getName() == null || tripLocationDto.getName().isEmpty()) {
            return ResponseEntity.badRequest().body("장소명이 존재하지 않습니다.");
        }
        if (tripLocationDto.getLatitude() == null) {
            return ResponseEntity.badRequest().body("위도가 존재하지 않습니다.");
        }

        if (tripLocationDto.getLongitude() == null) {
            return ResponseEntity.badRequest().body("경도가 존재하지 않습니다.");
        }


        tripLocationService.saveLocation(tripLocationDto);
        return ResponseEntity.ok("장소가 추가되었습니다.");
    }

}
