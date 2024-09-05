package com.example.omg_project.domain.chat.config;

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


@EnableKafka    // Kafka 사용을 활성화하는 애너테이션
@Configuration
public class KafkaProducerConfig {

    // Kafka 생산자 팩토리 빈 정의
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> config = new HashMap<>();   // 설정을 담을 맵 생성

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "54.253.66.249:9092");  // Kafka 서버 주소 설정

        // 키와 값의 시리얼라이저 설정
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);   // DefaultKafkaProducerFactory를 사용하여 생산자 팩토리 생성
    }

    // Kafka 템플릿 빈 정의
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());  // Kafka 템플릿 생성
    }
}
