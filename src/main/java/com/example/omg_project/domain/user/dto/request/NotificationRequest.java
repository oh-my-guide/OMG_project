package com.example.omg_project.domain.user.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {
    private Long userId;
    private String username;
    private String name; // 본명
    private String usernick; // 닉네임
}
