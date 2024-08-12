package com.example.omg_project.domain.trip.controller;

import com.example.omg_project.domain.trip.dto.ReadTripDTO;
import com.example.omg_project.domain.trip.entity.Team;
import com.example.omg_project.domain.trip.repository.TeamRepository;
import com.example.omg_project.domain.trip.service.TripService;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/trip")
public class TripController {

    private final TripService tripService;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    // 여행 일정 생성 페이지
    @GetMapping("/create")
    public String createTripPage() {
        return "trip/createtrip";
    }

    //여행 세부 정보 표시
    @GetMapping("/{id}")
    public String viewTripDetails(@PathVariable Long id, Model model, Principal principal) {
        ReadTripDTO tripDTO = tripService.getTripById(id);  // Trip 데이터를 DTO로 변환하여 가져옴
        String currentUserId = principal.getName(); // 현재 로그인한 사용자의 ID를 가져옵니다.

        // Team을 tripId를 사용하여 조회
        Team team = teamRepository.findByTripId(id)
                .orElseThrow(() -> new RuntimeException("Team not found for this trip"));

        // 팀 리더의 ID로 리더의 User 엔티티를 조회하여 username을 가져옴
        User leader = userRepository.findById(team.getLeader().getId())
                .orElseThrow(() -> new RuntimeException("Leader not found"));
        String leaderUsername = leader.getUsername();


        model.addAttribute("trip", tripDTO); // Trip 데이터를 모델에 추가
        model.addAttribute("team", team); // Trip 데이터를 모델에 추가

        model.addAttribute("currentUserId", currentUserId); // Trip 데이터를 모델에 추가
        model.addAttribute("leaderId", leaderUsername); // 리더의 username을 모델에 추가

        return "trip/viewtripdetails"; // HTML 파일 이름을 반환합니다.
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
