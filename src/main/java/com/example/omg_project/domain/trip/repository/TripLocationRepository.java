package com.example.omg_project.domain.trip.repository;

import com.example.omg_project.domain.trip.entity.TripLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripLocationRepository extends JpaRepository<TripLocation, Long> {
}