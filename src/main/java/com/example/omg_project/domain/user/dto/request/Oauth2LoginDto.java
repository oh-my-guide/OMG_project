package com.example.omg_project.domain.user.dto.request;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Oauth2LoginDto {

    private String phoneNumber; // 연락처
    private LocalDate birthdate; // 생년월일
    private String gender; // 성별
}
