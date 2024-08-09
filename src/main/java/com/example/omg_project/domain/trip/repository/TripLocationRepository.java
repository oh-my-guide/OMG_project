package com.example.omg_project.domain.trip.repository;

import com.example.omg_project.domain.trip.entity.TripDate;
import com.example.omg_project.domain.trip.entity.TripLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripLocationRepository extends JpaRepository<TripLocation, Long> {
//    List<TripLocation> findByTripDate(TripDate tripDate);
    void deleteAllByTripDateId(Long tripDateId);
}
