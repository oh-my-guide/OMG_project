package com.example.omg_project.domain.notification.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.stereotype.Component;

@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 메시지를 처리합니다. (예: 클라이언트로부터 구독 요청 수신)
        String payload = message.getPayload();
        // 메시지 처리 로직 추가
        System.out.println("Received message: " + payload);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 연결이 수립된 후의 작업을 처리합니다.
        System.out.println("WebSocket connection established: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 연결이 종료된 후의 작업을 처리합니다.
        System.out.println("WebSocket connection closed: " + session.getId());
    }
}
