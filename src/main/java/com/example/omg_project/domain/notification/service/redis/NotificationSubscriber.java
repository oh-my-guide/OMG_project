package com.example.omg_project.domain.notification.service.redis;

import com.example.omg_project.domain.notification.entity.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class NotificationSubscriber implements MessageListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // Redis에서 받은 메시지를 Notification 객체로 변환
        Notification notification = (Notification) deserialize(message.getBody());

        // WebSocket을 통해 알림 전송
        String notificationType = notification.getNotificationType();
        messagingTemplate.convertAndSend("/topic/notifications/" + notificationType + "/" + notification.getUserId(), notification);
    }

    private Object deserialize(byte[] bytes) {
        try {
            return objectMapper.readValue(bytes, Notification.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
