package com.example.omg_project.domain.trip.service.impl;

import com.example.omg_project.domain.trip.entity.City;
import com.example.omg_project.domain.trip.repository.CityRepository;
import com.example.omg_project.domain.trip.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;

    //도시 이름 검색
    @Override
    public Optional<City> getCityByName(String cityName) {
        return cityRepository.findByName(cityName);
    }

    @Override
    public Optional<City> getCityById(Long cityId) {
        return cityRepository.findById(cityId);
    }
}
