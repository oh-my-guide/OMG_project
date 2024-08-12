package com.example.omg_project.domain.user.dto.request;

import lombok.Getter;
import lombok.Setter;

// 이메일 인증 코드
@Getter @Setter
public class MailVerificationRequest {

    private String mail;
    private int code;
}
