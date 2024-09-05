package com.example.omg_project.domain.trip.service.impl;

import com.example.omg_project.domain.trip.entity.Team;
import com.example.omg_project.domain.trip.repository.TeamRepository;
import com.example.omg_project.domain.trip.service.TeamService;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.repository.UserRepository;
import com.example.omg_project.global.exception.CustomException;
import com.example.omg_project.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    /**
     * 초대 코드를 통해 팀을 조회하는 메서드
     *
     * @param inviteCode 조회할 팀의 초대 코드
     */
    @Override
    public Team findByInviteCode(String inviteCode) {
        return teamRepository.findByInviteCode(inviteCode);
    }

    /**
     * 초대 코드를 통해 팀에 사용자를 추가하는 메서드
     *
     * @param inviteCode 사용자가 참여할 팀의 초대 코드
     * @param userId     추가할 사용자의 ID
     */
    @Override
    public void addUserToTeam(String inviteCode, Long userId) {
        Team team = teamRepository.findByInviteCode(inviteCode);
        if (team == null) {
            throw new CustomException(ErrorCode.INVALID_INVITE_CODE);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        if (team != null && user != null) {
            team.getUsers().add(user);  // 팀에 사용자 추가
            user.getTeams().add(team);  // 사용자에 팀 추가
            teamRepository.save(team);  // 변경사항 저장
            userRepository.save(user);  // 변경사항 저장
        }
    }

    /**
     * 사용자를 팀에서 제거하는 메서드
     *
     * @param teamId 사용자가 제거될 팀의 ID
     * @param userId 제거할 사용자의 ID
     */
    @Override
    public void removeUserFromTeam(Long teamId, Long userId) {
        Team team = teamRepository.findById(teamId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);

        if (team != null && user != null) {
            if (team.getLeader().getId().equals(userId)) {
                throw new CustomException(ErrorCode.LEADER_CANNOT_LEAVE_TEAM);
            }
            team.getUsers().remove(user);
            user.getTeams().remove(team);
            teamRepository.save(team);
            userRepository.save(user);
        }
    }

    /**
     * 사용자의 팀 목록을 조회하는 메서드
     *
     * @param userId 조회할 사용자의 ID
     */
    @Override
    public List<Map<String, Object>> getUserTeams(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_EXCEPTION);
        }

        return user.getTeams().stream()
                .map(team -> {
                    Map<String, Object> teamData = new HashMap<>();
                    teamData.put("id", team.getId());
                    teamData.put("tripName", team.getTrip().getTripName());
                    teamData.put("chatRoomId", team.getChatRoom().getId());

                    // 현재 사용자가 팀의 리더인지 확인
                    boolean isLeader = team.getLeader().getId().equals(userId);
                    teamData.put("isLeader", isLeader);

                    if (isLeader) {
                        teamData.put("inviteCode", team.getInviteCode());
                    }

                    return teamData;
                })
                .collect(Collectors.toList());
    }

    /**
     * 팀의 채팅방 ID를 통해 팀을 조회하는 메서드
     *
     * @param chatRoomId 조회할 팀의 채팅방 ID
     */
    @Override
    public Team findByChatRoomId(Long chatRoomId) {
        Optional<Team> teamOptional = teamRepository.findByChatRoomId(chatRoomId);
        return teamOptional.orElseThrow(() ->
                new CustomException(ErrorCode.TEAM_NOT_FOUND_EXCEPTION)
        );
    }

    /**
     * 팀 ID를 통해 팀을 조회하는 메서드
     *
     * @param tripId 조회할 팀의 여행 일정 ID
     */
    @Override
    public Team findById(Long tripId) {
        return teamRepository.findById(tripId).orElse(null);
    }

    /**
     * 사용자가 팀에 속해 있는지 확인하는 메서드
     *
     * @param userId 확인할 사용자의 ID
     * @param teamId 확인할 팀의 ID
     */
    @Override
    public boolean isUserInTeam(Long userId, Long teamId) {
        // 팀에서 해당 사용자가 이미 속해 있는지 확인
        return teamRepository.existsByIdAndUsersId(teamId, userId);
    }
}

