package com.example.omg_project.domain.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDTO {
    private Long id;
    private String message;
    private String createdAt;
    private String userNickname;
}
