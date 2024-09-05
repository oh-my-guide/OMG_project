package com.example.omg_project.domain.trip.repository;

import com.example.omg_project.domain.trip.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

   Team findByInviteCode(String inviteCode);

    List<Team> findAllByTripId(Long tripId);
    // 특정 여행 일정과 연결된 팀을 찾는 메서드
    Optional<Team> findByTripId(Long tripId);

    Optional<Team> findByChatRoomId(Long chatRoomId);

    boolean existsByIdAndUsersId(Long teamId, Long userId);
}
