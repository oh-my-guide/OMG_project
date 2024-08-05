package com.example.omg_project.domain.chat.service.impl;

// 필요한 클래스 임포트
import com.example.omg_project.domain.chat.entity.ChatMessage;
import com.example.omg_project.domain.chat.entity.ChatRoom;
import com.example.omg_project.domain.chat.repository.ChatMessageRepository;
import com.example.omg_project.domain.chat.repository.ChatRoomRepository;
import com.example.omg_project.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

// 채팅 서비스 클래스
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    // ChatMessageRepository와 ChatRoomRepository 주입
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    // 메시지를 저장하는 메서드
    public ChatMessage saveMessage(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }

    // 특정 채팅방의 메시지를 조회하는 메서드
    public List<ChatMessage> getMessagesByRoomId(Long roomId) {
        return chatMessageRepository.findByChatRoomId(roomId);
    }

    // 채팅방을 생성하는 메서드
    public ChatRoom createRoom() {
        ChatRoom chatRoom = new ChatRoom();
        return chatRoomRepository.save(chatRoom);
    }
}