package com.example.omg_project.domain.chat.kafka;

import com.example.omg_project.domain.chat.dto.ChatMessageDTO;
import com.example.omg_project.domain.chat.entity.ChatMessage;
import com.example.omg_project.domain.chat.repository.ChatMessageRepository;
import com.example.omg_project.domain.chat.repository.ChatRoomRepository;
import com.example.omg_project.domain.chat.service.BadWordService;
import com.example.omg_project.domain.chat.websocket.WebSocketHandler;
import com.example.omg_project.domain.notification.service.NotificationService;
import com.example.omg_project.domain.trip.entity.Team;
import com.example.omg_project.domain.trip.repository.TeamRepository;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
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

/**
 * Kafka 메시지 리스너 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class ChatMessageListener {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final BadWordService badWordService;
    private final NotificationService notificationService;
    private final TeamRepository teamRepository;

    private static final Logger logger = Logger.getLogger(ChatMessageListener.class.getName());

    /**
     * Kafka 메시지를 수신하는 리스너 메서드
     *
     * @param message 수신된 메시지의 페이로드
     * @param topic   메시지가 수신된 토픽 이름
     * @throws Exception 메시지 처리 중 발생한 예외
     */
    @KafkaListener(topics = "chat_topic", groupId = "chat-room-listener")
    public void listen(@Payload String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) throws Exception {
        try {
            // 메시지에서 roomId를 추출
            String roomId = extractRoomIdFromMessage(message);

            // BadWordService를 이용해 메시지 필터링
            message = badWordService.filterMessage(message);

            // 메시지를 ChatMessage 객체로 변환
            ChatMessage chatMessage = parseMessage(message, roomId);

            // 변환된 ChatMessage 객체를 데이터베이스에 저장
            chatMessageRepository.save(chatMessage);
            message = message.split(":")[1] + ": " + parseJsonMessage(message.split(": ")[1]);

            Team team = teamRepository.findByChatRoomId(Long.parseLong(roomId)).orElseThrow();
            for (User user : team.getUsers()){
                if (!user.getUsernick().equals(chatMessage.getUserNickname())){
                    notificationService.createNotification(user, message, "CHAT", chatMessage.getId());
                }
            }

            // 해당 채팅방에 연결된 모든 클라이언트에게 메시지 브로드캐스트
            broadcastMessage(roomId, chatMessage);
        } catch (Exception e) {
            // 메시지 처리 중 오류가 발생하면 로깅
            logger.log(Level.SEVERE, "Error processing message", e);
        }
    }

    /**
     * 메시지에서 roomId를 추출
     *
     * @param message 원본 메시지 문자열 (형식: "roomId:userNickname:message")
     * @return 추출된 roomId
     */
    private String extractRoomIdFromMessage(String message) {
        // 메시지를 ':'을 기준으로 분리
        String[] parts = message.split(":", 3);

        // 메시지 형식이 올바르지 않으면 예외 발생
        if (parts.length < 3) {
            throw new IllegalArgumentException("올바른 메세지 형식이 아닙니다");
        }

        // roomId 반환
        return parts[0];
    }

    /**
     * 메시지 문자열을 ChatMessage 객체로 변환
     *
     * @param message 원본 메시지 문자열 (형식: "roomId:userNickname:message")
     * @param roomId  메시지가 속한 채팅방 ID
     * @return 변환된 ChatMessage 객체
     */
    private ChatMessage parseMessage(String message, String roomId) {
        // 메시지를 ':'을 기준으로 분리
        String[] parts = message.split(":", 3);

        // 메시지 형식이 올바르지 않으면 예외 발생
        if (parts.length < 3) {
            throw new IllegalArgumentException("올바른 메세지 형식이 아닙니다");
        }

        // ChatMessage 객체 생성 및 속성 설정
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(parts[2].trim());  // 메시지 내용 설정
        chatMessage.setUser(userRepository.findByUsernick(parts[1]));  // 사용자 정보 설정
        chatMessage.setChatRoom(chatRoomRepository.findById(Long.parseLong(roomId)).orElseThrow());  // 채팅방 정보 설정
        chatMessage.setUserNickname(parts[1]);  // 사용자 닉네임 설정

        return chatMessage;
    }

    /**
     * 채팅방에 연결된 모든 세션에 메시지를 전송
     *
     * @param roomId      메시지가 전송될 채팅방 ID
     * @param chatMessage 전송할 메시지 객체
     * @throws IOException 메시지 전송 중 발생한 입출력 예외
     */
    private void broadcastMessage(String roomId, ChatMessage chatMessage) throws IOException {
        // 해당 채팅방에 연결된 WebSocket 세션들을 가져옴
        Set<WebSocketSession> sessions = WebSocketHandler.getSessions(roomId);

        if (sessions != null) {
            // ChatMessage 객체를 ChatMessageDTO로 변환
            ChatMessageDTO chatMessageDto = convertToDto(chatMessage);

            // ChatMessageDTO를 JSON 문자열로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonMessage = objectMapper.writeValueAsString(chatMessageDto);

            // 각 WebSocket 세션에 메시지 전송
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(jsonMessage));
                }
            }
        }
    }

    /**
     * ChatMessage 객체를 ChatMessageDTO로 변환
     *
     * @param chatMessage 변환할 ChatMessage 객체
     * @return 변환된 ChatMessageDTO 객체
     */
    private ChatMessageDTO convertToDto(ChatMessage chatMessage) {
        // DTO 객체 생성 및 속성 설정
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setId(chatMessage.getId());
        dto.setMessage(chatMessage.getMessage());
        dto.setUserNickname(chatMessage.getUserNickname());
        dto.setCreatedAt(chatMessage.getCreatedAt().toString());

        return dto;
    }

    /**
     * JSON 문자열에서 메시지 필드 값 추출
     *
     * @param jsonString JSON 문자열 (형식: '{"message" : "안녕"}')
     * @return 추출된 메시지 내용
     */
    private String parseJsonMessage(String jsonString) {
        try {
            // ObjectMapper 인스턴스 생성
            ObjectMapper objectMapper = new ObjectMapper();

            // JSON 문자열을 JsonNode로 변환
            JsonNode jsonNode = objectMapper.readTree(jsonString);

            // "message" 필드에서 값 추출
            return jsonNode.get("message").asText();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error parsing JSON message", e);
            return "Unknown message";
        }
    }

}
