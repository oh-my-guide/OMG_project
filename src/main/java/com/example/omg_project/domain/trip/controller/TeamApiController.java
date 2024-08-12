package com.example.omg_project.domain.trip.controller;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamApiController {
    private final TeamService teamService;
    private final UserRepository userRepository;

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
        teamService.addUserToTeam(inviteCode, user.getId());

        return new ResponseEntity<>("팀에 성공적으로 가입되었습니다.", HttpStatus.OK);
    }
}

