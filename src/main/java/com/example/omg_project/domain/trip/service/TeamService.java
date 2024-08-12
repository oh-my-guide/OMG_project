package com.example.omg_project.domain.trip.service;

import com.example.omg_project.domain.trip.entity.Team;

public interface TeamService {
    Team saveTeam(Team team);
    Team findByInviteCode(String inviteCode);
    void addUserToTeam(String inviteCode, Long userId);
}