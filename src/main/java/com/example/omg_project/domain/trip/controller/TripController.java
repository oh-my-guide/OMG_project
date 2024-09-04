package com.example.omg_project.domain.trip.controller;

import com.example.omg_project.domain.trip.dto.ReadTripDTO;
import com.example.omg_project.domain.trip.entity.Team;
import com.example.omg_project.domain.trip.entity.Trip;
import com.example.omg_project.domain.trip.repository.TeamRepository;
import com.example.omg_project.domain.trip.service.TripService;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.repository.UserRepository;
import com.example.omg_project.domain.user.service.UserService;
import com.example.omg_project.global.jwt.util.JwtTokenizer;
import jakarta.servlet.http.HttpServletRequest;
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
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final JwtTokenizer jwtTokenizer;
    private final UserService userService;

    // 여행 일정 생성 페이지
    @GetMapping("/create")
    public String createTripPage(Model model, HttpServletRequest request) {
        String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
        String username = jwtTokenizer.getUsernameFromToken(accessToken);
        User user = userService.findByUsername(username).orElseThrow();

        model.addAttribute("user", user);
        return "trip/createtrip";
    }

    //여행 세부 정보 표시
    @GetMapping("/{id}")
    public String viewTripDetails(@PathVariable Long id, Model model, HttpServletRequest request) {
        // JWT 토큰에서 사용자 정보 추출
        String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
        String username = jwtTokenizer.getUsernameFromToken(accessToken);

        // 사용자 정보 조회
        User user = userService.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        // Trip 데이터를 DTO로 변환하여 가져옴
        ReadTripDTO tripDTO = tripService.getTripById(id);

        // Team을 tripId를 사용하여 조회
        Team team = teamRepository.findByTripId(id)
                .orElseThrow(() -> new RuntimeException("Team not found for this trip"));

        // 팀 리더의 ID로 리더의 User 엔티티를 조회하여 username을 가져옴
        User leader = userRepository.findById(team.getLeader().getId())
                .orElseThrow(() -> new RuntimeException("Leader not found"));
        String leaderUsername = leader.getUsername();

        // 모델에 데이터 추가
        model.addAttribute("user", user);
        model.addAttribute("trip", tripDTO);
        model.addAttribute("team", team);
        model.addAttribute("currentUserId", username); // 현재 사용자 username
        model.addAttribute("leaderId", leaderUsername); // 리더의 username

        return "trip/viewtripdetails"; // HTML 파일 이름을 반환합니다.
    }


    //사용자의 일정 리스트 표시
    @GetMapping("/user/{userId}")
    public String viewTripsByUserIdPage(@PathVariable Long userId, Model model, HttpServletRequest request) {
        List<ReadTripDTO> trips = tripService.getTripsByUserId(userId);
        String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
        String username = jwtTokenizer.getUsernameFromToken(accessToken);
        User user = userService.findByUsername(username).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("trips", trips);

        return "trip/usertrip";
    }

    //여행 일정 수정 페이지
    @GetMapping("/update/{id}")
    public String showUpdateTripPage(@PathVariable Long id, Model model, HttpServletRequest request) {
        ReadTripDTO trip = tripService.getTripById(id);
        String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
        String username = jwtTokenizer.getUsernameFromToken(accessToken);
        User user = userService.findByUsername(username).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("trip", trip);
        return "trip/updatetrip";
    }

    @GetMapping("/select")
    public String selectTripPage() {
        return "trip/createorjoin";
    }
}
