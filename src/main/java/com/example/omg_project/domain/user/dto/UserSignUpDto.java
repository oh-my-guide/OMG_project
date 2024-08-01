package com.example.omg_project.domain.user.dto;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignUpDto {

    private Long id;
    private String username; //이메일 ==> id
    private String name; // 사용자 이름
    private String password;
    private String passwordCheck;
    private String usernick; // 닉네임
    private LocalDate birthdate; // 생년월일
    private String gender;
    private String phoneNumber;
}