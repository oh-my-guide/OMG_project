package com.example.omg_project.domain.trip.service.impl;

import com.example.omg_project.domain.trip.entity.Team;
import com.example.omg_project.domain.trip.repository.TeamRepository;
import com.example.omg_project.domain.trip.service.TeamService;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;

    private final UserRepository userRepository;

    @Override
    public Team saveTeam(Team team) {
        return teamRepository.save(team);
    }

    @Override
    public Team findByInviteCode(String inviteCode) {
        return teamRepository.findByInviteCode(inviteCode);
    }

    @Override
    public void addUserToTeam(String inviteCode, Long userId) {
        Team team = teamRepository.findByInviteCode(inviteCode);
        User user = userRepository.findById(userId).orElse(null);

        if (team != null && user != null) {
            team.getUsers().add(user);  // 팀에 사용자 추가
            user.getTeams().add(team);  // 사용자에 팀 추가
            teamRepository.save(team);  // 변경사항 저장
            userRepository.save(user);  // 변경사항 저장
        }
    }
}
