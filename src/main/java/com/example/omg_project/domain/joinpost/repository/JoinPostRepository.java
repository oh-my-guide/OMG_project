package com.example.omg_project.domain.joinpost.repository;

import com.example.omg_project.domain.joinpost.entity.JoinPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JoinPostRepository extends JpaRepository<JoinPost, Long> {
    // 일행 게시글 중복 확인
    boolean existsJoinPostByTripId(Long tripId);
    // 특정 사용자의 전체 게시글 확인
    List<JoinPost> findJoinPostByUserId(Long userId);
    // 지역별 전체 게시글 조회
    List<JoinPost> findByTripCityName(String cityName);
}
