package com.example.omg_project.domain.chat.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.errors.TopicExistsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class ChatMessageProducer {

    private final KafkaTemplate<String, String> kafkaTemplate; // KafkaTemplate을 통해 메시지를 전송합니다.
    private final ChatMessageListener chatMessageListener;
    private final KafkaAdmin kafkaAdmin; // KafkaAdmin을 사용하여 Kafka 관련 관리 작업을 수행합니다.

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers; // Kafka 클러스터의 주소를 외부 설정 파일에서 주입받습니다.

    // AdminClient를 생성하는 메서드
    private AdminClient createAdminClient() {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers); // Kafka 서버 주소 설정
        return AdminClient.create(props); // AdminClient 생성
    }

    // 주어진 토픽이 존재하는지 확인하고, 존재하지 않으면 새로 생성하는 메서드
    private void createTopicIfNotExists(String topicName) {
        AdminClient adminClient = createAdminClient(); // AdminClient 인스턴스 생성
        try {
            // 현재 존재하는 토픽 목록을 가져옵니다.
            var existingTopics = adminClient.listTopics().names().get();

            // 주어진 토픽이 이미 존재하는지 확인합니다.
            if (!existingTopics.contains(topicName)) {
                // 토픽이 존재하지 않으면 새로 생성합니다.
                NewTopic newTopic = new NewTopic(topicName, 1, (short) 1); // 1개의 파티션과 1개의 복제본을 가진 새 토픽 생성
                adminClient.createTopics(Collections.singletonList(newTopic)).all().get(); // 새 토픽을 카프카에 생성
                System.out.println("토픽 생성 완료: " + topicName);

                // 새로운 토픽이 생성된 후 메타데이터를 강제로 갱신합니다.
                refreshProducerMetadata();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace(); // 예외 발생 시 스택 트레이스를 출력합니다.
        } finally {
            adminClient.close(); // 작업이 끝나면 AdminClient를 닫습니다.
        }
    }

    // 카프카 프로듀서의 메타데이터를 강제로 갱신하는 메서드
    private void refreshProducerMetadata() {
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(getProducerProps())) {
            producer.partitionsFor("dummy-topic"); // "dummy-topic"은 아무 곳에도 전송되지 않는 임시 토픽입니다.
        }
    }

    // 카프카 프로듀서의 속성을 반환하는 메서드
    private Properties getProducerProps() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        return props;
    }

    // 주어진 채팅방 ID와 메시지를 받아서 해당 Kafka 토픽으로 메시지를 전송하는 메서드
    public void sendMessage(String roomId, String message) {
        String topicName = "chat-room-" + roomId; // 채팅방 ID에 기반한 토픽 이름 생성
        createTopicIfNotExists(topicName); // 토픽이 존재하지 않으면 생성
        System.out.println("채팅방 ID: " + roomId + ", 토픽: " + topicName + ", 메시지: " + message);
        kafkaTemplate.send(topicName, message); // 메시지를 해당 토픽으로 전송
    }
}
