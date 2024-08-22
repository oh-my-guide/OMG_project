package com.example.omg_project.domain.joinpost.controller;

import com.example.omg_project.domain.joinpost.dto.JoinPostDto;
import com.example.omg_project.domain.joinpost.service.JoinPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/joinPosts")
@RequiredArgsConstructor
public class JoinPostApiController {
    private final JoinPostService joinPostService;

    /**
     * 일행 게시글 작성
     */
    @PostMapping
    public ResponseEntity<JoinPostDto.Response> createJoinPost(@RequestBody JoinPostDto.Request joinPostRequest) {
        JoinPostDto.Response joinPost = joinPostService.createJoinPost(joinPostRequest);
        return ResponseEntity.ok(joinPost);
    }

    /**
     * 일행 게시글 단건 조회
     */
    @GetMapping("/{postId}")
    public ResponseEntity<JoinPostDto.Response> getJoinPostById(@PathVariable(name = "postId") Long id) {
        JoinPostDto.Response joinPostById = joinPostService.findJoinPostById(id);
        return ResponseEntity.ok(joinPostById);
    }

    /**
     * 일행 게시글 전체 조회 또는 지역별 조회
     */
    @GetMapping
    public ResponseEntity<List<JoinPostDto.Response>> getAllJoinPosts(@RequestParam(required = false) String city) {
        List<JoinPostDto.Response> joinPosts;
        if (city != null && !city.isEmpty()) {
            joinPosts = joinPostService.findJoinPostsByCity(city);
        } else {
            joinPosts = joinPostService.findAllJoinPost();
        }
        return ResponseEntity.ok(joinPosts);
    }

    /**
     * 특정 사용자의 일행 게시글 전체 조회
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<JoinPostDto.Response>> getJoinPostsByUserId(@PathVariable Long userId) {
        List<JoinPostDto.Response> joinPosts = joinPostService.findJoinPostsByUserId(userId);
        return ResponseEntity.ok(joinPosts);
    }

    /**
     * 일행 게시글 수정
     */
    @PutMapping("/{postId}")
    public ResponseEntity<JoinPostDto.Response> updateJoinPost(@PathVariable(name = "postId") Long id, @RequestBody JoinPostDto.Request joinPostRequest) {
        JoinPostDto.Response updateJoinPost = joinPostService.updateJoinPost(id, joinPostRequest);
        return ResponseEntity.ok(updateJoinPost);
    }

    /**
     * 일행 게시글 삭제
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deleteJoinPostById(@PathVariable(name = "postId") Long id) {
        joinPostService.deleteJoinPost(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 일행 게시글 중복 확인
     */
    @GetMapping("/checkDuplicate/{tripId}")
    public ResponseEntity<Boolean> checkDuplicatePost(@PathVariable Long tripId) {
        boolean exists = joinPostService.existsJoinPostByTripId(tripId);
        return ResponseEntity.ok(exists);
    }

}
