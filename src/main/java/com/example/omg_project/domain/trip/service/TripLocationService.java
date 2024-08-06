package com.example.omg_project.domain.trip.service;

import com.example.omg_project.domain.trip.dto.TripLocationDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TripLocationService {

    void saveLocation(TripLocationDto tripLocationDto);
    void saveLocations(List<TripLocationDto> tripLocationDtoList);

}
