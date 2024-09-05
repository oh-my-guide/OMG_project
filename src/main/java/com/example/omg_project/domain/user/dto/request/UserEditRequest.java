package com.example.omg_project.domain.user.dto.request;

import lombok.*;

/**
 * 회원 정보 수정 dto
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEditRequest {

    private String usernick; // 닉네임
    private String phoneNumber; // 연락처
}
