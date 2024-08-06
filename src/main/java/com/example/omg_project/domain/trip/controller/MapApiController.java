package com.example.omg_project.domain.trip.controller;

import com.example.omg_project.domain.trip.dto.TripLocationDto;
import com.example.omg_project.domain.trip.service.TripLocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MapApiController {

    private final TripLocationService tripLocationService;

    /**
     * 선택된 개별 항목마다 저장
     */
//    @PostMapping("/api/save-location")
//    public ResponseEntity<String> saveLocation(@RequestBody TripLocationDto tripLocationDto) {
//        if (tripLocationDto.getName() == null || tripLocationDto.getName().isEmpty()) {
//            return ResponseEntity.badRequest().body("장소명이 존재하지 않습니다.");
//        }
//        if (tripLocationDto.getLatitude() == null) {
//            return ResponseEntity.badRequest().body("위도가 존재하지 않습니다.");
//        }
//
//        if (tripLocationDto.getLongitude() == null) {
//            return ResponseEntity.badRequest().body("경도가 존재하지 않습니다.");
//        }
//
//        tripLocationService.saveLocation(tripLocationDto);
//        return ResponseEntity.ok("장소가 추가되었습니다.");
//    }

    /**
     * 선택된 항목들의 리스트를 받아서 개별 저장
     */
    @PostMapping("/api/save-location")
    public ResponseEntity<String> saveLocation(@RequestBody List<TripLocationDto> tripLocationDtoList) {
        if (tripLocationDtoList == null || tripLocationDtoList.isEmpty()) {
            return ResponseEntity.badRequest().body("장소 목록이 비어 있습니다.");
        }

        for (TripLocationDto tripLocationDto : tripLocationDtoList) {
            if (tripLocationDto.getName() == null || tripLocationDto.getName().isEmpty()) {
                return ResponseEntity.badRequest().body("장소명이 존재하지 않습니다.");
            }
            if (tripLocationDto.getLatitude() == null) {
                return ResponseEntity.badRequest().body("위도가 존재하지 않습니다.");
            }

            if (tripLocationDto.getLongitude() == null) {
                return ResponseEntity.badRequest().body("경도가 존재하지 않습니다.");
            }
        }

        tripLocationService.saveLocations(tripLocationDtoList);
        return ResponseEntity.ok("장소들이 추가되었습니다.");
    }

}
