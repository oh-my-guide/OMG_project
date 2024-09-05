package com.example.omg_project.domain.user.dto.request;

import lombok.*;
import java.time.LocalDate;

/**
 * 소셜로그인 추가 정보 기입 dto
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Oauth2LoginRequest {

    private String phoneNumber; // 연락처
    private LocalDate birthdate; // 생년월일
    private String gender; // 성별
}
