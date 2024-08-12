package com.example.omg_project.domain.trip.repository;

import com.example.omg_project.domain.trip.entity.Team;
import com.example.omg_project.domain.trip.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByTrip(Trip trip);
    void deleteByTripId(Long tripId);
    List<Team> findAllByTripId(Long tripId);
    // 특정 여행 일정과 연결된 팀을 찾는 메서드
    Optional<Team> findByTripId(Long tripId);

    // 팀의 리더를 특정 사용자로 설정된 팀을 찾는 메서드
    Optional<Team> findByLeaderId(Long leaderId);
    List<Team> findByTripId(Long tripId);
    Optional<Team> findByChatRoomId(Long chatRoomId);

}
