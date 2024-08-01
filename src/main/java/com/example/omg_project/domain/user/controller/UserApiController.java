package com.example.omg_project.domain.user.controller;

import com.example.omg_project.domain.user.dto.UserSignUpDto;
import com.example.omg_project.domain.user.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserApiController {

    private final UserServiceImpl userServiceimpl;

    @GetMapping("/api/signup")
    public ResponseEntity<UserSignUpDto> getSignupForm() {
        UserSignUpDto userSignUpDto = new UserSignUpDto();
        return ResponseEntity.ok(userSignUpDto);
    }

    @PostMapping("/api/signup")
    public ResponseEntity<String> signup(@RequestBody UserSignUpDto userSignUpDto) {
        try {
            userServiceimpl.signUp(userSignUpDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공!!");
        } catch (RuntimeException e) {
            log.error("회원가입 실패!! :: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원가입 실패!! :: " + e.getMessage());
        }
    }
}
