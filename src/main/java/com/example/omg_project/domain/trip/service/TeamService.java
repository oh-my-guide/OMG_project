package com.example.omg_project.domain.trip.service;

import com.example.omg_project.domain.trip.entity.Team;

public interface TeamService {
    Team findByChatRoomId(Long chatRoomId);
}
