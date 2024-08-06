package com.example.omg_project.domain.chat.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

// Kafka 사용을 활성화하는 애너테이션
@EnableKafka
// 이 클래스가 설정 클래스임을 나타냄
@Configuration
public class KafkaConsumerConfig {

    // Kafka 소비자 팩토리 빈 정의
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        // 설정을 담을 맵 생성
        Map<String, Object> config = new HashMap<>();
        // Kafka 서버 주소 설정
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        // 그룹 ID 설정
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "chat-room-listener");
        // 키와 값의 디시리얼라이저 설정
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        // DefaultKafkaConsumerFactory를 사용하여 소비자 팩토리 생성
        return new DefaultKafkaConsumerFactory<>(config);
    }

    // Kafka 리스너 컨테이너 팩토리 빈 정의
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        // 리스너 컨테이너 팩토리 생성
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        // 소비자 팩토리 설정
        factory.setConsumerFactory(consumerFactory());
        // 리스너 컨테이너 팩토리 반환
        return factory;
    }
}
