package com.example.omg_project.domain.joinpost.repository;

import com.example.omg_project.domain.joinpost.entity.JoinPost;
import com.example.omg_project.domain.joinpost.entity.JoinPostLike;
import com.example.omg_project.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JoinPostLikeRepository extends JpaRepository<JoinPostLike, Long> {
    boolean existsByUserIdAndJoinPostId(Long userId, Long postId);

    int countByJoinPostId(Long postId);

    JoinPostLike findByUserAndJoinPost(User user, JoinPost joinPost);
}
