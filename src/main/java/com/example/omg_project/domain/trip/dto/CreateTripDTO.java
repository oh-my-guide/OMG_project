package com.example.omg_project.domain.trip.dto;

import com.example.omg_project.domain.trip.entity.Trip;
import com.example.omg_project.domain.trip.entity.TripDate;
import com.example.omg_project.domain.trip.entity.TripLocation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor

/**
 * 여행 정보를 담는 데이터 전송 객체 (DTO) 클래스
 *
 * 여행의 ID, 이름, 시작 날짜, 종료 날짜, 도시 ID, 그리고 날짜별 여행 일정 목록을 포함
 */
public class CreateTripDTO {
    private Long tripId;
    private String tripName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long cityId;
    private List<TripDateDTO> tripDates;


    public CreateTripDTO(Trip trip) {
        this.tripName = trip.getTripName();
        this.startDate = trip.getStartDate();
        this.endDate = trip.getEndDate();
        this.cityId = trip.getCity().getId();
        this.tripDates = trip.getTripDates().stream()
                .map(TripDateDTO::new)
                .collect(Collectors.toList());
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TripDateDTO {
        private LocalDate tripDate;
        private List<TripLocationDTO> tripLocations;


        public TripDateDTO(TripDate tripDate) {
            this.tripDate = tripDate.getTripDate();
            this.tripLocations = tripDate.getTripLocations().stream()
                    .map(TripLocationDTO::new)
                    .collect(Collectors.toList());
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TripLocationDTO {
        private String placeName;
        private BigDecimal latitude;
        private BigDecimal longitude;

        public TripLocationDTO(TripLocation tripLocation) {
            this.placeName = tripLocation.getPlaceName();
            this.latitude = tripLocation.getLatitude();
            this.longitude = tripLocation.getLongitude();
        }
    }
}
