package com.example.omg_project.domain.user.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * JWT 로그인 요청
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequest {

    @NotEmpty
    private String username;

    @NotEmpty
    private String password;
}