package com.example.omg_project.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username; // 이메일

    @Column(nullable = false, length = 100)
    private String password; // 비밀번호

    @Column(nullable = false, length = 100)
    private String name; // 본명

    @Column(nullable = false, unique = true, length = 100)
    private String usernick; // 닉네임

    @Column(nullable = false, length = 50)
    private String phoneNumber; // 연락처

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate = LocalDateTime.now(); // 가입일

    @Column(nullable = false)
    private LocalDate birthdate; // 생일

    @Column(nullable = false)
    private String gender; // 성별

    private String filename; // 파일 이름

    private String filepath; // 파일 경로
}