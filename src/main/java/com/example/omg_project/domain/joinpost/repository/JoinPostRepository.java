package com.example.omg_project.domain.joinpost.repository;

import com.example.omg_project.domain.joinpost.entity.JoinPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JoinPostRepository extends JpaRepository<JoinPost, Long> {
    // 일행 게시글 중복 확인
    boolean existsJoinPostByTripId(Long tripId);
}
