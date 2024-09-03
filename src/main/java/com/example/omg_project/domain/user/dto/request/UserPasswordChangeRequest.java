package com.example.omg_project.domain.user.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserPasswordChangeRequest {

    private String oldPassword;  // 현재 비밀번호
    private String newPassword;  // 새 비밀번호
}
