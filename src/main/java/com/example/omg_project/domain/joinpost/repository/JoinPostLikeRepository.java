package com.example.omg_project.domain.joinpost.repository;

import com.example.omg_project.domain.joinpost.entity.JoinPost;
import com.example.omg_project.domain.joinpost.entity.JoinPostLike;
import com.example.omg_project.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JoinPostLikeRepository extends JpaRepository<JoinPostLike, Long> {
    // 좋아요 여부 확인
    boolean existsByUserIdAndJoinPostId(Long userId, Long postId);
    // 좋아요 수 체크
    int countByJoinPostId(Long postId);
    // 좋아요 가져오기
    JoinPostLike findByUserAndJoinPost(User user, JoinPost joinPost);
}
