package com.example.omg_project.domain.chat.config;

// 필요한 클래스 임포트
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

// Kafka 사용을 활성화하는 애너테이션
@EnableKafka
// 이 클래스가 설정 클래스임을 나타냄
@Configuration
public class KafkaProducerConfig {

    // Kafka 생산자 팩토리 빈 정의
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        // 설정을 담을 맵 생성
        Map<String, Object> config = new HashMap<>();
        // Kafka 서버 주소 설정
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        // 키와 값의 시리얼라이저 설정
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // DefaultKafkaProducerFactory를 사용하여 생산자 팩토리 생성
        return new DefaultKafkaProducerFactory<>(config);
    }

    // Kafka 템플릿 빈 정의
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        // Kafka 템플릿 생성
        return new KafkaTemplate<>(producerFactory());
    }
}
