package com.example.omg_project.domain.trip.repository;

import com.example.omg_project.domain.trip.entity.Team;
import com.example.omg_project.domain.trip.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    @Query("SELECT t FROM Trip t JOIN Team team ON t.id = team.trip.id JOIN team.users u WHERE u.id = :userId")
    List<Trip> findByUserId(@Param("userId") Long userId);
    Optional<Trip> findById(Long id);
}