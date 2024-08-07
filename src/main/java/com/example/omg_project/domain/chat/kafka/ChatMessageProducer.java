package com.example.omg_project.domain.chat.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    // 팀 ID와 메시지를 받아 팀별 Kafka 토픽으로 전송하는 메서드
    public void sendMessage(String roomId, String message) {
        String topicName = "chat-room-" + roomId;
        System.out.println("채팅방 ID: " + roomId + ", 토픽: " + topicName + ", 메시지: " + message);
        kafkaTemplate.send(topicName, message);
    }
}
