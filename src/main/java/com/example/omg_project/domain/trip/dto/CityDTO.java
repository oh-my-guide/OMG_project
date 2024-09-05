package com.example.omg_project.domain.trip.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 도시 정보를 담는 데이터 전송 객체 (DTO) 클래스
 *
 * 도시의 ID, 이름, 그리고 도시와 관련된 여행 일정 목록 포함
 */
@Getter
@Setter
public class CityDTO {
    private Long id;
    private String name;
    private List<CreateTripDTO> trips;
}