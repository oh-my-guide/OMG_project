package com.example.omg_project.domain.trip.controller;

import com.example.omg_project.domain.trip.entity.Team;
import com.example.omg_project.domain.trip.repository.TeamRepository;
import com.example.omg_project.domain.trip.service.TeamService;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/team")
@RequiredArgsConstructor
public class TeamApiController {
    private final TeamService teamService;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;


    @PostMapping("/join")
    public ResponseEntity<String> joinTeam(@RequestParam String inviteCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            username = userDetails.getUsername();
        }

        if (username == null) {
            return new ResponseEntity<>("인증된 사용자가 없습니다.", HttpStatus.UNAUTHORIZED);
        }

        // 사용자 이름으로 User 엔티티를 찾음
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));



        // 팀에 사용자 추가
        try {
            // 팀에 사용자 추가
            teamService.addUserToTeam(inviteCode, user.getId());
            return new ResponseEntity<>("팀에 성공적으로 가입되었습니다.", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("잘못된 초대 코드입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/myteam")
    public ResponseEntity<List<Map<String, Object>>> getMyTeams() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            username = userDetails.getUsername();
        }

        if (username == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        List<Map<String, Object>> teams = teamService.getUserTeams(user.getId());
        return new ResponseEntity<>(teams, HttpStatus.OK);
    }

    @PostMapping("/leave")
    public ResponseEntity<String> leaveTeam(@RequestBody Map<String, Long> request) {
        Long teamId = request.get("teamId");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            username = userDetails.getUsername();
        }

        if (username == null) {
            return new ResponseEntity<>("인증된 사용자가 없습니다.", HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        teamService.removeUserFromTeam(teamId, user.getId());
        return new ResponseEntity<>("팀에서 성공적으로 탈퇴하였습니다.", HttpStatus.OK);
    }
}

