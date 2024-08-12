package com.example.omg_project.domain.user.dto.request;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEditDto {

    private String usernick; // 닉네임
    private LocalDate birthdate; // 생년월일
    private String gender; // 성별
    private String phoneNumber; // 연락처
}
