package com.example.omg_project.domain.notification.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;
import org.springframework.stereotype.Component;

/**
 * WebSocket을 통해 알림을 처리하는 핸들러 클래스
 * 클라이언트와의 WebSocket 연결을 관리하고, 메시지를 처리합니다.
 */
@Component
@Slf4j
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    /**
     * 클라이언트로부터 받은 텍스트 메시지를 처리하는 메서드
     *
     * @param session 현재 WebSocket 세션
     * @param message 클라이언트로부터 받은 텍스트 메시지
     * @throws Exception 메시지 처리 중 발생할 수 있는 예외
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("Received message: " + payload);
    }

    /**
     * WebSocket 연결이 수립된 후 호출되는 메서드
     *
     * @param session 연결된 WebSocket 세션
     * @throws Exception 연결 수립 중 발생할 수 있는 예외
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket connection established: " + session.getId());
    }

    /**
     * WebSocket 연결이 종료된 후 호출되는 메서드
     *
     * @param session 종료된 WebSocket 세션
     * @param status 연결 종료 상태
     * @throws Exception 연결 종료 중 발생할 수 있는 예외
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("WebSocket connection closed: " + session.getId());
    }
}
