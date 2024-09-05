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

    /**
     * 도시 이름을 통해 도시 정보를 조회하는 메서드
     *
     * @param cityName 조회할 도시의 이름
     */
    @Override
    public Optional<City> getCityByName(String cityName) {
        return cityRepository.findByName(cityName);
    }
}
