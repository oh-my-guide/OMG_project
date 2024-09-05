package com.example.omg_project.domain.trip.dto;

import com.example.omg_project.domain.trip.entity.City;
import com.example.omg_project.domain.trip.entity.Trip;
import com.example.omg_project.domain.trip.entity.TripDate;
import com.example.omg_project.domain.trip.entity.TripLocation;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter

/**
 * 읽기 전용 여행 정보를 담는 데이터 전송 객체(DTO) 클래스
 */
public class ReadTripDTO {
    private Long id;
    private String tripName;
    private LocalDate startDate;
    private LocalDate endDate;
    private CityDTO city;
    private List<TripDateDTO> tripDates;

    public static ReadTripDTO fromEntity(Trip trip) {
        ReadTripDTO dto = new ReadTripDTO();
        dto.setId(trip.getId());
        dto.setTripName(trip.getTripName());
        dto.setStartDate(trip.getStartDate());
        dto.setEndDate(trip.getEndDate());
        dto.setCity(CityDTO.fromEntity(trip.getCity()));
        dto.setTripDates(trip.getTripDates().stream().map(TripDateDTO::fromEntity).collect(Collectors.toList()));
        return dto;
    }

    @Getter
    @Setter
    public static class CityDTO {
        private Long id;
        private String name;

        public static CityDTO fromEntity(City city) {
            CityDTO dto = new CityDTO();
            dto.setId(city.getId());
            dto.setName(city.getName());
            return dto;
        }
    }

    @Getter
    @Setter
    public static class TripDateDTO {
        private Long id;
        private LocalDate tripDate;
        private List<TripLocationDTO> tripLocations;

        public static TripDateDTO fromEntity(TripDate tripDate) {
            TripDateDTO dto = new TripDateDTO();
            dto.setId(tripDate.getId());
            dto.setTripDate(tripDate.getTripDate());
            dto.setTripLocations(tripDate.getTripLocations().stream().map(TripLocationDTO::fromEntity).collect(Collectors.toList()));
            return dto;
        }
    }

    @Getter
    @Setter
    public static class TripLocationDTO {
        private Long id;
        private String placeName;
        private double latitude;
        private double longitude;

        public static TripLocationDTO fromEntity(TripLocation tripLocation) {
            TripLocationDTO dto = new TripLocationDTO();
            dto.setId(tripLocation.getId());
            dto.setPlaceName(tripLocation.getPlaceName());
            dto.setLatitude(tripLocation.getLatitude().doubleValue());
            dto.setLongitude(tripLocation.getLongitude().doubleValue());
            return dto;
        }
    }
}
