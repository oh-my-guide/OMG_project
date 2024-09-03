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


@EnableKafka    // Kafka 사용을 활성화하는 애너테이션
@Configuration
public class KafkaConsumerConfig {

    // Kafka 소비자 팩토리 빈 정의
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {

        Map<String, Object> config = new HashMap<>();   // 설정을 담을 맵 생성

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "3.39.43.41:9092");  // Kafka 서버 주소 설정

        config.put(ConsumerConfig.GROUP_ID_CONFIG, "chat-room-listener");   // 그룹 ID 설정

        // 키와 값의 디시리얼라이저 설정
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");


        return new DefaultKafkaConsumerFactory<>(config);   // DefaultKafkaConsumerFactory를 사용하여 소비자 팩토리 생성
    }

    // Kafka 리스너 컨테이너 팩토리 빈 정의
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();  // 리스너 컨테이너 팩토리 생성

        factory.setConsumerFactory(consumerFactory());  // 소비자 팩토리 설정

        return factory; // 리스너 컨테이너 팩토리 반환
    }
}
