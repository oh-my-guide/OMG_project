package com.example.omg_project.domain.chat.service;

import com.example.omg_project.domain.chat.entity.ChatMessage;
import com.example.omg_project.domain.chat.entity.ChatRoom;
import com.example.omg_project.domain.chat.repository.ChatMessageRepository;
import com.example.omg_project.domain.chat.repository.ChatRoomRepository;

import java.util.List;

public interface ChatService {
    ChatMessage saveMessage(ChatMessage chatMessage);
    List<ChatMessage> getMessagesByRoomId(Long roomId);
    ChatRoom createRoom();
}
