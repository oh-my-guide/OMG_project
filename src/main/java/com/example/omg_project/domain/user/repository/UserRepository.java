package com.example.omg_project.domain.user.repository;

import com.example.omg_project.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    boolean existsByUsernick(String userName);
    boolean existsByUsername(String email);
}
