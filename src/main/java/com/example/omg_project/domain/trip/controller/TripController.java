package com.example.omg_project.domain.trip.controller;

import com.example.omg_project.domain.trip.dto.CreateTripDTO;
import com.example.omg_project.domain.trip.dto.ReadTripDTO;
import com.example.omg_project.domain.trip.entity.Trip;
import com.example.omg_project.domain.trip.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    // 여행 일정 생성 페이지 이동
    @GetMapping("/createtrip")
    public String createTripPage() {
        return "trip/createtrip";
    }


    @GetMapping("/{id}")
    public String viewTripDetailsPage(@PathVariable Long id, Model model) {
        ReadTripDTO trip = tripService.getTripById(id);
        if (trip != null) {
            model.addAttribute("trip", trip);
            return "trip/viewtripdetails";
        } else {
            return "trip/notfound";
        }
    }

    @GetMapping("/user/{userId}")
    public String viewTripsByUserIdPage(@PathVariable Long userId, Model model) {
        List<ReadTripDTO> trips = tripService.getTripsByUserId(userId);
        model.addAttribute("trips", trips);
        return "trip/usertrip";
    }
}
