package com.example.omg_project.domain.chat.websocket;

import com.example.omg_project.domain.chat.kafka.ChatMessageProducer;
import com.example.omg_project.domain.user.service.UserService;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.global.jwt.util.JwtTokenizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

    //웹소켓으로 접속한 세션들을 팀별로 관리하기 위한 저장소를 생성한다.
    private static final Map<String, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    private final ChatMessageProducer chatMessageProducer;
    private final UserService userService;
    private final JwtTokenizer jwtTokenizer;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String roomId = (String) session.getAttributes().get("roomId");
        //팀끼리 동기화 시켜준다
        roomSessions.computeIfAbsent(roomId, k -> Collections.synchronizedSet(new HashSet<>())).add(session);
        System.out.println("세션 연결 성공 :: " + session.getId() + " 채팅방 ID: " + roomId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String roomId = (String) session.getAttributes().get("roomId");
        Set<WebSocketSession> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            sessions.remove(session);
        }
    }

    // 텍스트 메시지 처리 메서드
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

        chatMessageProducer.sendMessage(roomId, nickname + ": " + payload);
    }

    public static Set<WebSocketSession> getSessions(String roomId) {
        return roomSessions.get(roomId);
    }
}
