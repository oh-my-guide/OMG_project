package com.example.omg_project.domain.chat.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
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
        String topicName = "chatTopic"; // 채팅 메시지를 전송할 Kafka 토픽 이름 설정

        log.info("채팅방 ID: " + roomId + ", 토픽: " + topicName + ", 메시지: " + message);

        // roomId와 메시지를 결합하여 최종 메시지 형식을 생성
        message = roomId + ":" + message;

        // KafkaTemplate을 사용해 메시지를 해당 토픽으로 전송
        kafkaTemplate.send(topicName, message);
    }
}
