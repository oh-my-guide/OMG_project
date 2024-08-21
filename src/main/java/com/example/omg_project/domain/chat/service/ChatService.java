package com.example.omg_project.domain.chat.service;

import com.example.omg_project.domain.chat.dto.ChatMessageDTO;
import com.example.omg_project.domain.chat.entity.ChatMessage;
import com.example.omg_project.domain.chat.entity.ChatRoom;
import com.example.omg_project.domain.user.entity.User;

import java.util.List;

public interface ChatService {
    boolean existsById(Long roomId);
    List<ChatMessageDTO> getMessagesByRoomId(Long roomId);
    ChatMessageDTO convertToDTO(ChatMessage chatMessage);
    void validateUserInChatRoom(Long roomId, User user);
    String findTripName(Long roomId);
}
