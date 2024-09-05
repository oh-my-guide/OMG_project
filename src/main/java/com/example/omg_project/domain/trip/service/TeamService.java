package com.example.omg_project.domain.trip.service;

import com.example.omg_project.domain.trip.entity.Team;

import java.util.List;
import java.util.Map;

public interface TeamService {
    void removeUserFromTeam(Long teamId, Long userId);

    List<Map<String, Object>> getUserTeams(Long userId);

    Team findByInviteCode(String inviteCode);

    void addUserToTeam(String inviteCode, Long userId);

    Team findByChatRoomId(Long chatRoomId);

    Team findById(Long tripId);
    boolean isUserInTeam(Long userId, Long teamId);
}
