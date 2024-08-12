package com.example.omg_project.domain.trip.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CityDTO {
    private Long id;
    private String name;
    private List<CreateTripDTO> trips;
}