package com.example.omg_project.domain.trip.service.impl;

import com.example.omg_project.domain.trip.entity.Team;
import com.example.omg_project.domain.trip.repository.TeamRepository;
import com.example.omg_project.domain.trip.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;

    // 팀의 채팅방 ID와 사용자 ID를 기반으로 사용자 포함 여부 확인
    @Override
    public Team findByChatRoomId(Long chatRoomId) {
        Optional<Team> teamOptional = teamRepository.findByChatRoomId(chatRoomId);

        return teamOptional.orElseThrow(() -> new NoSuchElementException("채팅방 ID에 해당하는 팀을 찾을 수 없습니다. ID: " + chatRoomId));
    }
}