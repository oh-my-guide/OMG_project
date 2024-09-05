package com.example.omg_project.domain.chat.websocket;

import com.example.omg_project.domain.chat.kafka.ChatMessageProducer;
import com.example.omg_project.domain.user.service.UserService;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.global.jwt.util.JwtTokenizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    // 웹소켓으로 접속한 세션들을 팀별로 관리하기 위한 저장소
    private static final Map<String, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    private final ChatMessageProducer chatMessageProducer;
    private final UserService userService;
    private final JwtTokenizer jwtTokenizer;

    /**
     * 웹소켓 연결이 성립된 후 호출됩니다.
     *
     * @param session 웹소켓 세션
     * @throws Exception 연결 성립 중 발생할 수 있는 예외
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String roomId = (String) session.getAttributes().get("roomId");
        // 방별로 세션을 관리하여 동기화된 세션 집합을 생성
        roomSessions.computeIfAbsent(roomId, k -> Collections.synchronizedSet(new HashSet<>())).add(session);
        log.info("세션 연결 성공 :: " + session.getId() + " 채팅방 ID: " + roomId);
    }

    /**
     * 웹소켓 연결이 종료된 후 호출됩니다.
     *
     * @param session 웹소켓 세션
     * @param status 연결 종료 상태
     * @throws Exception 연결 종료 중 발생할 수 있는 예외
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String roomId = (String) session.getAttributes().get("roomId");
        Set<WebSocketSession> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            sessions.remove(session);  // 세션을 방에서 제거
        }
    }

    /**
     * 텍스트 메시지를 처리하는 메서드입니다.
     *
     * @param session 웹소켓 세션
     * @param message 텍스트 메시지
     * @throws Exception 메시지 처리 중 발생할 수 있는 예외
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();

        // JWT 토큰에서 사용자 정보를 추출
        String token = (String) session.getAttributes().get("jwtToken");
        String username = "Unknown user";
        String nickname = "Unknown user";

        if (token != null && !jwtTokenizer.isAccessTokenExpired(token)) {
            username = jwtTokenizer.getUsernameFromToken(token);
            Optional<User> userOptional = userService.findByUsername(username);
            User user = userOptional.orElseThrow();
            nickname = user.getUsernick();
        }

        String roomId = (String) session.getAttributes().get("roomId");

        // 채팅 메시지를 Kafka를 통해 전송
        chatMessageProducer.sendMessage(roomId, nickname + ": " + payload);
    }

    /**
     * 지정된 채팅방 ID에 대한 모든 웹소켓 세션을 반환합니다.
     *
     * @param roomId 채팅방 ID
     * @return 웹소켓 세션 집합
     */
    public static Set<WebSocketSession> getSessions(String roomId) {
        return roomSessions.get(roomId);
    }
}
