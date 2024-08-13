package com.example.omg_project.domain.trip.controller;

import com.example.omg_project.domain.trip.entity.Trip;
import com.example.omg_project.domain.trip.entity.Team;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.service.UserService;
import com.example.omg_project.global.jwt.util.JwtTokenizer;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventApiController {

    private final UserService userService;
    private final JwtTokenizer jwtTokenizer;

    @GetMapping
    public List<Map<String, Object>> getEvents(HttpServletRequest request) {
        // 쿠키에서 accessToken 찾기
        String accessToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    break;  // accessToken을 찾으면 반복문 종료
                }
            }
        }

        // accessToken이 없으면 예외 발생
        if (accessToken == null) {
            throw new RuntimeException("Access token이 없습니다.");
        }

        String username = jwtTokenizer.getUsernameFromToken(accessToken);
        User user = userService.findByUsername(username).orElseThrow();
        Set<Team> teamSet = user.getTeams();

        // 각 팀에 해당하는 여행을 찾고, 결과를 List로 반환
        List<Map<String, Object>> events = new ArrayList<>();
        for (Team team : teamSet) {
            Trip trip = team.getTrip();
            if (trip != null) {
                events.add(Map.of(
                        "title", trip.getTripName(),
                        "start", trip.getStartDate().toString(),
                        "end", trip.getEndDate() != null ? trip.getEndDate().toString() : null
                ));
            }
        }

        return events;
    }
}

