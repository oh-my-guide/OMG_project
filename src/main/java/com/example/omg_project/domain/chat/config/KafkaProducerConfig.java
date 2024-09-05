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

@EnableKafka
@Configuration
public class KafkaProducerConfig {

    /**
     * Kafka 생산자 팩토리 빈을 정의합니다.
     *
     * @return ProducerFactory<String, String> Kafka 생산자 팩토리 인스턴스
     */
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> config = new HashMap<>();   // Kafka 설정을 담을 맵 생성

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "ec2-43-202-189-185.ap-northeast-2.compute.amazonaws.com/:9092");  // Kafka 서버 주소 설정

        // 키와 값의 시리얼라이저 설정
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    /**
     * Kafka 템플릿 빈을 정의합니다.
     *
     * @return KafkaTemplate<String, String> Kafka 템플릿 인스턴스
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
