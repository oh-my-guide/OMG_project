package com.example.omg_project.domain.chat.kafka;

// 필요한 클래스 임포트
import com.example.omg_project.domain.chat.entity.ChatMessage;
import com.example.omg_project.domain.chat.repository.ChatMessageRepository;
import com.example.omg_project.domain.chat.repository.ChatRoomRepository;
import com.example.omg_project.domain.chat.repository.UserRepository;
import com.example.omg_project.domain.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

// Kafka 메시지 리스너 서비스 클래스
@Service
@RequiredArgsConstructor
public class ChatMessageListener {

    // ChatMessageRepository와 ObjectMapper 주입
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ObjectMapper objectMapper;

    private static final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
    private static final Logger logger = Logger.getLogger(ChatMessageListener.class.getName());
    private final UserRepository userRepository;

    // 웹소켓 세션 추가 메서드
    public static void addSession(WebSocketSession session) {
        sessions.add(session);
    }

    // 웹소켓 세션 제거 메서드
    public static void removeSession(WebSocketSession session) {
        sessions.remove(session);
    }

    // Kafka 메시지를 수신하는 리스너 메서드
    @KafkaListener(topics = "chat_topic", groupId = "group_id")
    public void listen(String message) throws Exception {
        try {
            // 수신된 메시지를 ChatMessage 객체로 변환
            ChatMessage chatMessage = parseMessage(message);

            // 변환된 ChatMessage 객체를 데이터베이스에 저장
            chatMessageRepository.save(chatMessage);

            // 클라이언트에게 메시지를 브로드캐스트
            broadcastMessage(message);
        } catch (Exception e) {
            // 에러 발생 시 로깅
            logger.log(Level.SEVERE, "Error processing message", e);
        }
    }

    // 메시지 문자열을 ChatMessage 객체로 변환하는 메서드
    private ChatMessage parseMessage(String message) {
        String[] parts = message.split(":", 2);
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid message format");
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(parts[1].trim());
        chatMessage.setUser(userRepository.findByUsernick(parts[0])); // 제대로된 인증이 구현되면 그때 수정할 수 있도록
        chatMessage.setChatRoom(chatRoomRepository.findById(1L).orElseThrow()); // 채팅방 ID는 예시, 실제 구현 시 채팅방 ID를 처리해야 함
        chatMessage.setUserNickname(parts[0]);
        System.out.println(parts[0]);
        User user = userRepository.findByUsernick(parts[0]);
        System.out.println(user.getUsername());
        return chatMessage;
    }

    // 모든 클라이언트에게 메시지를 브로드캐스트하는 메서드
    private void broadcastMessage(String message) throws IOException {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        }
    }
}
