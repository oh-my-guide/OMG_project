package com.example.omg_project.domain.trip.service;

import com.example.omg_project.domain.trip.entity.City;

import java.util.Optional;

public interface CityService {
    Optional<City> getCityByName(String cityName);
}
