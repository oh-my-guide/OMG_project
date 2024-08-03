package com.example.omg_project.domain.user.service;

import com.example.omg_project.domain.user.dto.UserSignUpDto;
import com.example.omg_project.domain.user.entity.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {

    Optional<User> findById(Long id);

    void signUp(UserSignUpDto userSignUpDto);

    Optional<User> findByUsername(String username);

    void deleteUser(String email);
}
