package com.example.omg_project.domain.chat.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 주어진 채팅방 ID와 메시지를 받아서 해당 Kafka 토픽으로 메시지를 전송하는 메서드
     *
     * @param roomId  메시지가 전송될 채팅방 ID
     * @param message 전송할 메시지 내용
     */
    public void sendMessage(String roomId, String message) {
        String topicName = "chat_topic"; // 채팅 메시지를 전송할 Kafka 토픽 이름 설정

        System.out.println("채팅방 ID: " + roomId + ", 토픽: " + topicName + ", 메시지: " + message);

        // roomId와 메시지를 결합하여 최종 메시지 형식을 만듭니다.
        message = roomId + ":" + message;

        // KafkaTemplate을 사용해 메시지를 해당 토픽으로 전송합니다.
        kafkaTemplate.send(topicName, message);
    }
}
