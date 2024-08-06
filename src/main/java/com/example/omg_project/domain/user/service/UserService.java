package com.example.omg_project.domain.user.service;

import com.example.omg_project.domain.user.dto.UserSignUpDto;
import com.example.omg_project.domain.user.entity.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {

    Optional<User> findById(Long id);

    void signUp(UserSignUpDto userSignUpDto); // 회원가입

    Optional<User> findByUsername(String username); // 아이디 찾기

    void deleteUser(String username); // 회원 탈퇴

    boolean existsByUsernick(String usernick); // 닉네임 중복 확인
}
