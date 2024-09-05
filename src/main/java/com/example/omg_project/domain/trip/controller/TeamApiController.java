package com.example.omg_project.domain.trip.controller;

import com.example.omg_project.domain.trip.entity.Team;
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

    /**
     * 팀 가입 요청
     *
     * @param inviteCode 팀 가입을 위한 초대 코드
     */
    @PostMapping("/join")
    public ResponseEntity<String> joinTeam(@RequestParam String inviteCode) {
        try {
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

            Team team = teamService.findByInviteCode(inviteCode);
            if (team == null) {
                return new ResponseEntity<>("잘못된 초대 코드입니다.", HttpStatus.BAD_REQUEST);
            }

            // 사용자가 이미 팀에 속해 있는지 확인
            if (teamService.isUserInTeam(user.getId(), team.getId())) {
                return new ResponseEntity<>("이미 팀에 가입되어 있습니다.", HttpStatus.BAD_REQUEST);
            }

            // 팀에 사용자 추가
            teamService.addUserToTeam(inviteCode, user.getId());
            return new ResponseEntity<>("팀에 성공적으로 가입되었습니다.", HttpStatus.OK);

        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("잘못된 초대 코드입니다.", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 현재 사용자가 가입한 팀 목록 조회
     *
     * @return 현재 사용자가 가입한 팀 목록과 HTTP 상태 코드
     */
    @GetMapping("/myteam")
    public ResponseEntity<List<Map<String, Object>>> getMyTeams() {
        try {
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

        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 팀 탈퇴 요청 처리
     *
     * @param request 팀 탈퇴를 위한 요청 데이터 (teamId)
     */
    @PostMapping("/leave")
    public ResponseEntity<String> leaveTeam(@RequestBody Map<String, Long> request) {
        try {
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

        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}


