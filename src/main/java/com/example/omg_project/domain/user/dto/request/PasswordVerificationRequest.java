package com.example.omg_project.domain.user.dto.request;

import lombok.Getter;
import lombok.Setter;

// 임시 비밀번호 발급 Dto
@Getter @Setter
public class PasswordVerificationRequest {

    private String mail;
    private String tempPassword;
}