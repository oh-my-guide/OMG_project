package com.example.omg_project.domain.trip.repository;

import com.example.omg_project.domain.trip.entity.Team;
import com.example.omg_project.domain.trip.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByTrip(Trip trip);
    void deleteByTrip(Trip trip);
}
