package com.example.omg_project.domain.chat.service;

import com.example.omg_project.domain.chat.dto.ChatMessageDTO;
import com.example.omg_project.domain.chat.entity.ChatRoom;

import java.util.List;

public interface ChatService {
    List<ChatMessageDTO> getMessagesByRoomId(Long roomId);
    ChatRoom createRoom();
}
