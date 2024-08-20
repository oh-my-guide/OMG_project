package com.example.omg_project.domain.trip.service.impl;

import com.example.omg_project.domain.chat.entity.ChatRoom;
import com.example.omg_project.domain.chat.repository.ChatRoomRepository;
import com.example.omg_project.domain.trip.dto.CreateTripDTO;
import com.example.omg_project.domain.trip.dto.ReadTripDTO;
import com.example.omg_project.domain.trip.dto.UpdateTripDTO;
import com.example.omg_project.domain.trip.entity.*;
import com.example.omg_project.domain.trip.repository.*;
import com.example.omg_project.domain.trip.service.TripService;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.repository.UserRepository;
import com.example.omg_project.global.jwt.util.JwtTokenizer;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {
    private final TripRepository tripRepository;
    private final CityRepository cityRepository;
    private final TripDateRepository tripDateRepository;
    private final TripLocationRepository tripLocationRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final JwtTokenizer jwtTokenizer;

    //여행 일정 생성
    @Override
    @Transactional
    public Trip createTrip(CreateTripDTO createTripDTO, String jwtToken) {
        Claims claims = jwtTokenizer.parseAccessToken(jwtToken);
        Long leaderId = claims.get("userId", Long.class);
        User leader = userRepository.findById(leaderId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        //도시 조회
        City city = cityRepository.findById(createTripDTO.getCityId())
                .orElseThrow(() -> new RuntimeException("City not found"));

        //여행 일정 생성
        Trip trip = new Trip();
        trip.setTripName(createTripDTO.getTripName());
        trip.setStartDate(createTripDTO.getStartDate());
        trip.setEndDate(createTripDTO.getEndDate());
        trip.setCity(city);

        Trip savedTrip = tripRepository.save(trip);

        //날짜 정보 저장
        for (CreateTripDTO.TripDateDTO tripDateDTO : createTripDTO.getTripDates()) {
            TripDate tripDate = new TripDate();
            tripDate.setTripDate(tripDateDTO.getTripDate());
            tripDate.setTrip(savedTrip);

            //며칠 차인지 계산
            int day = (int) ChronoUnit.DAYS.between(createTripDTO.getStartDate(), tripDateDTO.getTripDate()) + 1;
            tripDate.setDay(day);

            TripDate savedTripDate = tripDateRepository.save(tripDate);

            //위치 정보 저장
            for (CreateTripDTO.TripLocationDTO tripLocationDTO : tripDateDTO.getTripLocations()) {
                TripLocation tripLocation = new TripLocation();
                tripLocation.setPlaceName(tripLocationDTO.getPlaceName());
                tripLocation.setLatitude(tripLocationDTO.getLatitude());
                tripLocation.setLongitude(tripLocationDTO.getLongitude());
                tripLocation.setTripDate(savedTripDate);

                tripLocationRepository.save(tripLocation);
            }
        }

        //채팅룸 생성
        ChatRoom chatRoom = new ChatRoom();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        Team team = new Team();
        team.setTrip(savedTrip);
        team.setLeader(leader);
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

    //초대코드 생성
    private String generateInviteCode() {
        SecureRandom random = new SecureRandom();
        int num = random.nextInt(100000);
        return String.format("INVITE-%05d", num);
    }

    @Override
    @Transactional
    public void deleteTrip(Long tripId) {
        log.info("Deleting trip with ID: {}", tripId);

        // 관련된 Team 삭제
        List<Team> teams = teamRepository.findAllByTripId(tripId);
        log.info("Found {} teams associated with trip ID: {}", teams.size(), tripId);
        for (Team team : teams) {
            // ManyToMany 관계 정리
            for (User user : team.getUsers()) {
                user.getTeams().remove(team);
            }
            team.getUsers().clear(); // ManyToMany 관계 정리
            Long chatRoomId = team.getChatRoom().getId();
            log.info("Deleting chat room with ID: {}", chatRoomId);
            teamRepository.delete(team);
        }

        // 관련된 TripDates 및 TripLocations 삭제
        List<TripDate> tripDates = tripDateRepository.findByTripId(tripId);
        log.info("Found {} trip dates associated with trip ID: {}", tripDates.size(), tripId);
        for (TripDate tripDate : tripDates) {
            log.info("Deleting trip locations for trip date ID: {}", tripDate.getId());
            tripLocationRepository.deleteAllByTripDateId(tripDate.getId());
            log.info("Deleting trip date with ID: {}", tripDate.getId());
            tripDateRepository.delete(tripDate);
        }

        // Trip 삭제
        log.info("Deleting trip with ID: {}", tripId);
        tripRepository.deleteById(tripId);
        log.info("Successfully deleted trip with ID: {}", tripId);
    }

    //각각의 여행 일정 조회
    @Override
    @Transactional
    public ReadTripDTO getTripById(Long id) {
        Trip trip = tripRepository.findById(id).orElseThrow(() -> new RuntimeException("Trip not found"));
        return ReadTripDTO.fromEntity(trip);
    }

    //사용자의 여행 일정 조회
    @Override
    public List<ReadTripDTO> getTripsByUserId(Long userId) {
        List<Trip> trips = tripRepository.findByUserId(userId);
        return trips.stream().map(ReadTripDTO::fromEntity).collect(Collectors.toList());
    }

    //여행 일정 수정 (날짜 고정)
    @Override
    @Transactional
    public UpdateTripDTO updateTrip(Long id, UpdateTripDTO updateTripDTO) {
        log.info("Starting updateTrip with id: {}", id);

        Optional<Trip> tripOptional = tripRepository.findById(id);
        if (tripOptional.isPresent()) {
            Trip trip = tripOptional.get();
            log.info("Found Trip: {}", trip);

            trip.setTripName(updateTripDTO.getTripName());
            trip.setStartDate(updateTripDTO.getStartDate());
            trip.setEndDate(updateTripDTO.getEndDate());
            log.info("Updated Trip basic info: {}", trip);

            // 기존 TripDates 및 TripLocations 업데이트 로직
            Map<Long, TripDate> existingTripDates = new HashMap<>();
            for (TripDate tripDate : trip.getTripDates()) {
                existingTripDates.put(tripDate.getId(), tripDate);
            }
            log.info("Existing TripDates loaded: {}", existingTripDates.keySet());

            for (UpdateTripDTO.TripDateDTO tripDateDTO : updateTripDTO.getTripDates()) {
                TripDate tripDate;
                if (tripDateDTO.getId() != null && existingTripDates.containsKey(tripDateDTO.getId())) {
                    tripDate = existingTripDates.get(tripDateDTO.getId());
                    tripDate.setTripDate(tripDateDTO.getTripDate());
                    existingTripDates.remove(tripDateDTO.getId());
                    log.info("Updated existing TripDate: {}", tripDate);
                } else {
                    tripDate = new TripDate();
                    tripDate.setTripDate(tripDateDTO.getTripDate());
                    tripDate.setTrip(trip);
                    tripDate.setTripLocations(new ArrayList<>());
                    trip.getTripDates().add(tripDate);
                    log.info("Created new TripDate: {}", tripDate);
                }

                Map<Long, TripLocation> existingTripLocations = new HashMap<>();
                for (TripLocation tripLocation : tripDate.getTripLocations()) {
                    existingTripLocations.put(tripLocation.getId(), tripLocation);
                }
                log.info("Existing TripLocations for TripDate {}: {}", tripDate.getId(), existingTripLocations.keySet());

                for (UpdateTripDTO.TripLocationDTO tripLocationDTO : tripDateDTO.getTripLocations()) {
                    TripLocation tripLocation;
                    if (tripLocationDTO.getId() != null && existingTripLocations.containsKey(tripLocationDTO.getId())) {
                        tripLocation = existingTripLocations.get(tripLocationDTO.getId());
                        tripLocation.setPlaceName(tripLocationDTO.getPlaceName());
                        tripLocation.setLatitude(tripLocationDTO.getLatitude());
                        tripLocation.setLongitude(tripLocationDTO.getLongitude());
                        existingTripLocations.remove(tripLocationDTO.getId());
                        log.info("Updated existing TripLocation: {}", tripLocation);
                    } else {
                        tripLocation = new TripLocation();
                        tripLocation.setPlaceName(tripLocationDTO.getPlaceName());
                        tripLocation.setLatitude(tripLocationDTO.getLatitude());
                        tripLocation.setLongitude(tripLocationDTO.getLongitude());
                        tripLocation.setTripDate(tripDate);
                        tripDate.getTripLocations().add(tripLocation);
                        log.info("Created new TripLocation: {}", tripLocation);
                    }
                }

                // 제거된 TripLocations 삭제
                for (TripLocation tripLocation : existingTripLocations.values()) {
                    tripDate.getTripLocations().remove(tripLocation);
                    tripLocationRepository.delete(tripLocation);
                    log.info("Deleted TripLocation: {}", tripLocation);
                }
            }

            // 제거된 TripDates 삭제
            for (TripDate tripDate : existingTripDates.values()) {
                trip.getTripDates().remove(tripDate);
                tripDateRepository.delete(tripDate);
                log.info("Deleted TripDate: {}", tripDate);
            }

            tripRepository.save(trip);
            log.info("Trip saved successfully: {}", trip);
            return updateTripDTO;
        } else {
            log.warn("Trip not found with id: {}", id);
            return null;
        }
    }

    //여행 일정 복사
    @Override
    @Transactional
    public Trip copyTripToUser(Long tripId, String jwtToken) {
        log.info("copyTripToUser 시작 - tripId: {}, jwtToken: {}", tripId, jwtToken);
            // 기존 일정 조회
            Trip originalTrip = tripRepository.findById(tripId)
                    .orElseThrow(() -> {
                        log.error("Trip not found with id: {}", tripId);
                        return new RuntimeException("Trip not found");
                    });
            log.info("Found original trip: {}", originalTrip);

            // 여행 이름에 " -copy" 추가
            String newTripName = originalTrip.getTripName() + " -copy";

            // JWT 토큰에서 사용자 정보 추출
            Claims claims = jwtTokenizer.parseAccessToken(jwtToken);
            Long leaderId = claims.get("userId", Long.class);
            log.info("Parsed JWT Token - userId: {}", leaderId);

            // 사용자 정보 조회
            User leader = userRepository.findById(leaderId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            log.info("Found user: {}", leader);

            // 도시에 대한 정보 가져오기
            City city = cityRepository.findById(originalTrip.getCity().getId())
                    .orElseThrow(() -> new RuntimeException("City not found"));
            log.info("Found city: {}", city);

            // 여행 일정 생성
            Trip trip = new Trip();
            trip.setTripName(newTripName);
            trip.setStartDate(originalTrip.getStartDate());
            trip.setEndDate(originalTrip.getEndDate());
            trip.setCity(city);
            log.info("Initialized new Trip entity: {}", trip);

            Trip savedTrip = tripRepository.save(trip);
            log.info("Saved Trip entity: {}", savedTrip);

            // 날짜 정보 저장
            for (TripDate originalTripDate : originalTrip.getTripDates()) {
                TripDate tripDate = new TripDate();
                tripDate.setTripDate(originalTripDate.getTripDate());
                tripDate.setTrip(savedTrip);
                tripDate.setDay(originalTripDate.getDay());

                TripDate savedTripDate = tripDateRepository.save(tripDate);
                log.info("Saved TripDate entity: {}", savedTripDate);

                // 위치 정보 저장
                for (TripLocation originalTripLocation : originalTripDate.getTripLocations()) {
                    TripLocation tripLocation = new TripLocation();
                    tripLocation.setPlaceName(originalTripLocation.getPlaceName());
                    tripLocation.setLatitude(originalTripLocation.getLatitude());
                    tripLocation.setLongitude(originalTripLocation.getLongitude());
                    tripLocation.setTripDate(savedTripDate);

                    tripLocationRepository.save(tripLocation);
                    log.info("Saved TripLocation entity: {}", tripLocation);
                }
            }

            ChatRoom chatRoom = new ChatRoom();
            ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

            Team team = new Team();
            team.setTrip(savedTrip);
            team.setLeader(leader);
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
    }



