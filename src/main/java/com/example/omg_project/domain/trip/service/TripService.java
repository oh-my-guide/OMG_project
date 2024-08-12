package com.example.omg_project.domain.trip.service;

import com.example.omg_project.domain.trip.dto.CreateTripDTO;
import com.example.omg_project.domain.trip.dto.ReadTripDTO;
import com.example.omg_project.domain.trip.dto.UpdateTripDTO;
import com.example.omg_project.domain.trip.entity.Trip;

import java.util.List;

public interface TripService {
    Trip createTrip(CreateTripDTO createTripDTO, String jwtToken);
    ReadTripDTO getTripById(Long id);
    List<ReadTripDTO> getTripsByUserId(Long userId);
    void deleteTrip(Long tripId);
    UpdateTripDTO updateTrip(Long id, UpdateTripDTO updateTripDTO);

}