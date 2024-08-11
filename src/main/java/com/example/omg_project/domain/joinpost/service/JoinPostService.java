package com.example.omg_project.domain.joinpost.service;

import com.example.omg_project.domain.joinpost.dto.JoinPostDto;

import java.util.List;

public interface JoinPostService {
    // 일행 게시글 작성
    JoinPostDto.Response createJoinPost(JoinPostDto.Request joinPostRequest);
    // 일행 게시글 전체 조회
    List<JoinPostDto.Response> findAllJoinPost();
    // 일행 게시글 상세 조회
    JoinPostDto.Response findJoinPostById(Long id);
    // 일행 게시글 수정
    JoinPostDto.Response updateJoinPost(Long id, JoinPostDto.Request joinPostRequest);
    // 일행 게시글 삭제
    void deleteJoinPost(Long id);
    // 일행 게시글 중복 확인
    boolean existsJoinPostByTripId(Long tripId);
}
