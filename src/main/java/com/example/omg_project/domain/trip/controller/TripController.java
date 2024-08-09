package com.example.omg_project.domain.trip.controller;

import com.example.omg_project.domain.trip.dto.ReadTripDTO;
import com.example.omg_project.domain.trip.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/trip")
public class TripController {

    private final TripService tripService;

    // 여행 일정 생성 페이지
    @GetMapping("/create")
    public String createTripPage() {
        return "trip/createtrip";
    }

    //여행 세부 정보 표시
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

    //사용자의 일정 리스트 표시
    @GetMapping("/user/{userId}")
    public String viewTripsByUserIdPage(@PathVariable Long userId, Model model) {
        List<ReadTripDTO> trips = tripService.getTripsByUserId(userId);
        model.addAttribute("trips", trips);
        return "trip/usertrip";
    }

    //여행 일정 수정 페이지
    @GetMapping("/update/{id}")
    public String showUpdateTripPage(@PathVariable Long id, Model model) {
        ReadTripDTO trip = tripService.getTripById(id);
        model.addAttribute("trip", trip);
        return "trip/updateTrip";
    }
}
