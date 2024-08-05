package com.example.omg_project.domain.chat.websocket;

// 필요한 클래스 임포트
import com.example.omg_project.domain.chat.kafka.ChatMessageListener;
import com.example.omg_project.domain.chat.kafka.ChatMessageProducer;
import com.example.omg_project.domain.chat.service.UserService;
import com.example.omg_project.domain.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

// WebSocket 핸들러를 Spring 컴포넌트로 등록
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {
    //웹소켓으로 접속한 세션들을 관리하기 위한 저장소를 생성한다.
    private static final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("세션 연결 성공 :: "+session.getId());
        ChatMessageListener.addSession(session);
    }


    // Kafka 메시지 생산자와 JSON 변환기 주입
    private final ChatMessageProducer chatMessageProducer;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    // 생성자를 통해 의존성 주입
//    public WebSocketHandler(ChatMessageProducer chatMessageProducer, ObjectMapper objectMapper) {
//        this.chatMessageProducer = chatMessageProducer;
//        this.objectMapper = objectMapper;
//    }

    // 텍스트 메시지 처리 메서드
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 수신된 메시지 페이로드 추출
        String payload = message.getPayload();

        //현재 인증된 사용자 정보 가져옴.
        SecurityContext securityContext = (SecurityContext) session.getAttributes().get("SPRING_SECURITY_CONTEXT");
        String username = "Unknown user";
        String nickname = "Unknown user";

        if(securityContext != null && securityContext.getAuthentication() != null &&
                securityContext.getAuthentication().getPrincipal() instanceof UserDetails){
            UserDetails userDetails = (UserDetails) securityContext.getAuthentication().getPrincipal();
            username = userDetails.getUsername();
            User user = userService.findByUsername(username);
            nickname = user.getUsernick();
        }
        System.out.println("유저 잘 가져오나" + username + nickname);
        // Kafka 메시지 생산자를 통해 메시지 전송
        chatMessageProducer.sendMessage(nickname + ": " + payload);
    }
}
