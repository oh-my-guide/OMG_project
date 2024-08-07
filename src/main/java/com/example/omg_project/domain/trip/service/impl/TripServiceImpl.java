package com.example.omg_project.domain.trip.service.impl;

import com.example.omg_project.domain.chat.entity.ChatRoom;
import com.example.omg_project.domain.chat.repository.ChatRepository;
import com.example.omg_project.domain.trip.dto.CreateTripDTO;
import com.example.omg_project.domain.trip.dto.ReadTripDTO;
import com.example.omg_project.domain.trip.entity.*;
import com.example.omg_project.domain.trip.repository.*;
import com.example.omg_project.domain.trip.service.TripService;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {
    private final TripRepository tripRepository;
    private final CityRepository cityRepository;
    private final TripDateRepository tripDateRepository;
    private final TripLocationRepository tripLocationRepository;
    private final ChatRepository chatRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Trip createTrip(CreateTripDTO createTripDTO, User leader) {
        City city = cityRepository.findById(createTripDTO.getCityId())
                .orElseThrow(() -> new RuntimeException("City not found"));

        Trip trip = new Trip();
        trip.setTripName(createTripDTO.getTripName());
        trip.setStartDate(createTripDTO.getStartDate());
        trip.setEndDate(createTripDTO.getEndDate());
        trip.setCity(city);

        Trip savedTrip = tripRepository.save(trip);

        for (CreateTripDTO.TripDateDTO tripDateDTO : createTripDTO.getTripDates()) {
            TripDate tripDate = new TripDate();
            tripDate.setTripDate(tripDateDTO.getTripDate());
            tripDate.setTrip(savedTrip);

            int day = (int) ChronoUnit.DAYS.between(createTripDTO.getStartDate(), tripDateDTO.getTripDate()) + 1;
            tripDate.setDay(day);

            TripDate savedTripDate = tripDateRepository.save(tripDate);

            for (CreateTripDTO.TripLocationDTO tripLocationDTO : tripDateDTO.getTripLocations()) {
                TripLocation tripLocation = new TripLocation();
                tripLocation.setPlaceName(tripLocationDTO.getPlaceName());
                tripLocation.setLatitude(tripLocationDTO.getLatitude());
                tripLocation.setLongitude(tripLocationDTO.getLongitude());
                tripLocation.setTripDate(savedTripDate);

                tripLocationRepository.save(tripLocation);
            }
        }

        ChatRoom chatRoom = new ChatRoom();
        ChatRoom savedChatRoom = chatRepository.save(chatRoom);

        Team team = new Team();
        team.setTrip(savedTrip);
        team.setLeaderId(leader);
        team.setInviteCode(generateInviteCode());
        team.setChatRoom(savedChatRoom);

        // 팀과 리더를 사용자 목록에 추가
        team.getUsers().add(leader);

        Team savedTeam = teamRepository.save(team);

        // 사용자의 팀 목록에 새 팀 추가
        leader.getTeams().add(savedTeam);
        userRepository.save(leader); // 변경된 사용자 정보 저장

        return savedTrip;
    }

    private String generateInviteCode() {
        SecureRandom random = new SecureRandom();
        int num = random.nextInt(100000);
        return String.format("INVITE-%05d", num);
    }
    @Override
    @Transactional // 트랜잭션을 관리합니다.
    public void deleteTrip(Long id) {
        // 1. 관련된 Team 데이터 삭제
        teamRepository.deleteByTripId(id);

        // 2. Trip 데이터 삭제
        tripRepository.deleteById(id);
    }





    @Override
    public ReadTripDTO getTripById(Long id) {
        Trip trip = tripRepository.findById(id).orElseThrow(() -> new RuntimeException("Trip not found"));
        return ReadTripDTO.fromEntity(trip);
    }

    @Override
    public List<ReadTripDTO> getTripsByUserId(Long userId) {
        List<Trip> trips = tripRepository.findByUserId(userId);
        return trips.stream().map(ReadTripDTO::fromEntity).collect(Collectors.toList());
    }
}
