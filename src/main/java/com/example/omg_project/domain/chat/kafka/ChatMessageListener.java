package com.example.omg_project.domain.chat.kafka;

import com.example.omg_project.domain.chat.dto.ChatMessageDTO;
import com.example.omg_project.domain.chat.entity.ChatMessage;
import com.example.omg_project.domain.chat.repository.ChatMessageRepository;
import com.example.omg_project.domain.chat.repository.ChatRoomRepository;
import com.example.omg_project.domain.chat.websocket.WebSocketHandler;
import com.example.omg_project.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

// Kafka 메시지 리스너 서비스 클래스
@Service
@RequiredArgsConstructor
public class ChatMessageListener {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    private static final Logger logger = Logger.getLogger(ChatMessageListener.class.getName());

    @KafkaListener(topicPattern = "chat-room-.*", groupId = "chat-room-listener")
    public void listen(@Payload String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) throws Exception {
        try {
            String roomId = topic.substring("chat-room-".length());
            ChatMessage chatMessage = parseMessage(message, roomId);
            chatMessageRepository.save(chatMessage);
            broadcastMessage(roomId, chatMessage);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing message", e);
        }
    }

    private ChatMessage parseMessage(String message, String roomId) {
        String[] parts = message.split(":", 2);
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid message format");
        }
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(parts[1].trim());
        chatMessage.setUser(userRepository.findByUsernick(parts[0]));
        chatMessage.setChatRoom(chatRoomRepository.findById((Long.parseLong(roomId))).orElseThrow());
        chatMessage.setUserNickname(parts[0]);
        return chatMessage;
    }

    private ChatMessageDTO convertToDto(ChatMessage chatMessage) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setId(chatMessage.getId());
        dto.setMessage(chatMessage.getMessage());
        dto.setUserNickname(chatMessage.getUserNickname());
        dto.setCreatedAt(chatMessage.getCreatedAt().toString());
        return dto;
    }

    private void broadcastMessage(String roomId, ChatMessage chatMessage) throws IOException {
        Set<WebSocketSession> sessions = WebSocketHandler.getSessions(roomId);
        if (sessions != null) {
            // DTO로 변환
            ChatMessageDTO chatMessageDto = convertToDto(chatMessage);

            // DTO를 JSON으로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonMessage = objectMapper.writeValueAsString(chatMessageDto);
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(jsonMessage));
                }
            }
        }
    }
}

