package com.example.omg_project.domain.chat.kafka;

// 필요한 클래스 임포트
import com.example.omg_project.domain.chat.entity.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

// Kafka 메시지 생산자 서비스 클래스
@Service
public class ChatMessageProducer {

    // Kafka 토픽 이름 설정
    private static final String TOPIC = "chat_topic";

    // Kafka 템플릿 주입
    private final KafkaTemplate<String, String> kafkaTemplate;

    // 생성자를 통해 Kafka 템플릿 주입
    public ChatMessageProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // 메시지를 Kafka 토픽으로 전송하는 메서드
    public void sendMessage(String message) {
        System.out.println("들어옴");
        kafkaTemplate.send(TOPIC, message);
    }
}
