package com.example.omg_project.domain.trip.controller;

import com.example.omg_project.domain.trip.entity.Trip;
import com.example.omg_project.domain.trip.entity.Team;
import com.example.omg_project.domain.trip.entity.TripDate;
import com.example.omg_project.domain.trip.entity.TripLocation;
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
        String accessToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }

        if (accessToken == null) {
            throw new RuntimeException("Access token이 없습니다.");
        }

        String username = jwtTokenizer.getUsernameFromToken(accessToken);
        User user = userService.findByUsername(username).orElseThrow();
        Set<Team> teamSet = user.getTeams();

        List<Map<String, Object>> events = new ArrayList<>();
        for (Team team : teamSet) {
            Trip trip = team.getTrip();
            if (trip != null) {
                Map<String, Object> event = new HashMap<>();
                event.put("title", trip.getTripName());
                event.put("start", trip.getStartDate().toString());
                event.put("end", trip.getEndDate().plusDays(1).toString());

                // 모든 위치 정보를 리스트로 포함 (단일 이벤트에 대해 하나의 리스트)
                List<Map<String, Object>> locations = new ArrayList<>();
                for (TripDate tripDate : trip.getTripDates()) {
                    for (TripLocation location : tripDate.getTripLocations()) {
                        locations.add(Map.of(
                                "latitude", location.getLatitude(),
                                "longitude", location.getLongitude(),
                                "placeName", location.getPlaceName()
                        ));
                    }
                }

                event.put("extendedProps", Map.of("locations", locations));
                events.add(event);
            }
        }
        return events;
    }
}
