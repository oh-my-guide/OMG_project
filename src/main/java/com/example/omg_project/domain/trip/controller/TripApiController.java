package com.example.omg_project.domain.trip.controller;

import com.example.omg_project.domain.trip.dto.CreateTripDTO;
import com.example.omg_project.domain.trip.dto.ReadTripDTO;
import com.example.omg_project.domain.trip.dto.UpdateTripDTO;
import com.example.omg_project.domain.trip.entity.Trip;
import com.example.omg_project.domain.trip.service.TripService;
import com.example.omg_project.global.jwt.util.JwtTokenizer;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripApiController {
    private final TripService tripService;
    private final JwtTokenizer jwtTokenizer;

    //여행 일정 생성
    @PostMapping
    public ResponseEntity<?> createTrip(@RequestBody CreateTripDTO createTripDTO, HttpServletRequest request) {
        try {
            // 쿠키에서 JWT 토큰 가져오기
            String jwtToken = null;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("accessToken".equals(cookie.getName())) {
                        jwtToken = cookie.getValue();
                        break;
                    }
                }
            }
            if (jwtToken == null) {
                throw new RuntimeException("JWT 토큰이 없습니다.");
            }

            System.out.println("Received JWT Token: " + jwtToken);

            // JWT 토큰 파싱하여 사용자 정보 추출
            Claims claims = jwtTokenizer.parseAccessToken(jwtToken);
            claims.getSubject();

            // 여행 일정 생성
            tripService.createTrip(createTripDTO, jwtToken);

            Map<String, String> successResponse = new HashMap<>();
            successResponse.put("message", "여행 일정이 생성되었습니다.");
            return ResponseEntity.ok(successResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "여행 일정 생성 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // 다른 사용자의 여행 일정 복사
    @PostMapping("/{tripId}/copy")
    public ResponseEntity<?> copyTripToCurrentUser(@PathVariable Long tripId, HttpServletRequest request) {
        try {
            // 쿠키에서 JWT 토큰 가져오기2
            String jwtToken = null;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("accessToken".equals(cookie.getName())) {
                        jwtToken = cookie.getValue();
                        break;
                    }
                }
            }
            if (jwtToken == null) {
                throw new RuntimeException("JWT 토큰이 없습니다.");
            }

            System.out.println("Received JWT Token: " + jwtToken);
            // JWT 토큰 파싱하여 사용자 정보 추출
            Claims claims = jwtTokenizer.parseAccessToken(jwtToken);
            claims.getSubject();

            // 일정 복사
            Trip copiedTrip = tripService.copyTripToUser(tripId, jwtToken);

            Map<String, String> successResponse = new HashMap<>();
            successResponse.put("message", "이 여행 일정을 나의 일정에 저장하였습니다.");
            successResponse.put("tripId", String.valueOf(copiedTrip.getId()));
            return ResponseEntity.ok(successResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "여행 일정 복사 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    //tripId로 일정 조회
    @GetMapping("/{id}")
    public ResponseEntity<ReadTripDTO> getTripById(@PathVariable Long id) {
        ReadTripDTO trip = tripService.getTripById(id);
        if (trip != null) {
            return ResponseEntity.ok(trip);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //userId로 일정 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReadTripDTO>> getTripsByUserId(@PathVariable Long userId) {
        List<ReadTripDTO> trips = tripService.getTripsByUserId(userId);
        return ResponseEntity.ok(trips);
    }

    //여행 일정 수정
    @PutMapping("/{id}")
    public ResponseEntity<UpdateTripDTO> updateTrip(@PathVariable Long id, @RequestBody UpdateTripDTO updateTripDTO) {
        UpdateTripDTO updatedTrip = tripService.updateTrip(id, updateTripDTO);
        if (updatedTrip != null) {
            return ResponseEntity.ok(updatedTrip);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //여행 일정 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Long id) {
        tripService.deleteTrip(id);
        return ResponseEntity.noContent().build();
    }
}

