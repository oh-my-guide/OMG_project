package com.example.omg_project.domain.trip.controller;

import com.example.omg_project.domain.trip.dto.CreateTripDTO;
import com.example.omg_project.domain.trip.dto.ReadTripDTO;
import com.example.omg_project.domain.trip.entity.Trip;
import com.example.omg_project.domain.trip.service.TripService;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripApiController {
    private final TripService tripService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createTrip(@RequestBody CreateTripDTO createTripDTO, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Trip trip = tripService.createTrip(createTripDTO, user);

            Map<String, String> successResponse = new HashMap<>();
            successResponse.put("message", "여행 일정이 생성되었습니다.");
            return ResponseEntity.ok(successResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "여행 일정 생성 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<ReadTripDTO> getTripById(@PathVariable Long id) {
        ReadTripDTO trip = tripService.getTripById(id);
        if (trip != null) {
            return ResponseEntity.ok(trip);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReadTripDTO>> getTripsByUserId(@PathVariable Long userId) {
        List<ReadTripDTO> trips = tripService.getTripsByUserId(userId);
        return ResponseEntity.ok(trips);
    }
}

