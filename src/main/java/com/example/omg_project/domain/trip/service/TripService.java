package com.example.omg_project.domain.trip.service;

import com.example.omg_project.domain.trip.dto.CreateTripDTO;
import com.example.omg_project.domain.trip.dto.ReadTripDTO;
import com.example.omg_project.domain.trip.entity.Trip;
import com.example.omg_project.domain.user.entity.User;

import java.util.List;

public interface TripService {
    Trip createTrip(CreateTripDTO createTripDTO, User leader);
    ReadTripDTO getTripById(Long id);
    List<ReadTripDTO> getTripsByUserId(Long userId);
}