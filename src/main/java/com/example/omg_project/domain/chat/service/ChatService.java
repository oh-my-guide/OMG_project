package com.example.omg_project.domain.chat.service;

import com.example.omg_project.domain.chat.dto.ChatMessageDTO;
import com.example.omg_project.domain.chat.entity.ChatMessage;
import com.example.omg_project.domain.chat.entity.ChatRoom;

import java.util.List;

public interface ChatService {
    boolean existsById(Long roomId);
    List<ChatMessageDTO> getMessagesByRoomId(Long roomId);
    ChatMessageDTO convertToDTO(ChatMessage chatMessage);
    ChatRoom createRoom();
    void validateUserInChatRoom(Long roomId, String accessToken);
}
