package com.example.omg_project.domain.chat.service.impl;

import com.example.omg_project.domain.chat.dto.ChatMessageDTO;
import com.example.omg_project.domain.chat.entity.ChatMessage;
import com.example.omg_project.domain.chat.entity.ChatRoom;
import com.example.omg_project.domain.chat.repository.ChatMessageRepository;
import com.example.omg_project.domain.chat.repository.ChatRoomRepository;
import com.example.omg_project.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 채팅 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    // 해당 채팅방의 모든 메세지를 ChatMessageDTO로 변경하여 반환하는 메서드
    public List<ChatMessageDTO> getMessagesByRoomId(Long roomId) {
        List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomId(roomId);
        return chatMessages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ChatMessage를 ChatMessageDTO로 변경
    private ChatMessageDTO convertToDTO(ChatMessage chatMessage) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setId(chatMessage.getId());
        dto.setUserNickname(chatMessage.getUserNickname());
        dto.setMessage(chatMessage.getMessage());
        dto.setCreatedAt(chatMessage.getCreatedAt().toString());
        return dto;
    }

    // 채팅방을 생성하는 메서드
    public ChatRoom createRoom() {
        ChatRoom chatRoom = new ChatRoom();
        return chatRoomRepository.save(chatRoom);
    }
}