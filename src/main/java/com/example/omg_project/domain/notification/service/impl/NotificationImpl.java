package com.example.omg_project.domain.notification.service.impl;

import com.example.omg_project.domain.notification.entity.Notification;
import com.example.omg_project.domain.notification.repository.NotificationRepository;
import com.example.omg_project.domain.notification.service.NotificationService;
import com.example.omg_project.domain.user.dto.request.NotificationRequest;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.repository.UserRepository;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void createNotification(User user, String message, String notificationType, Long relatedEntityId) throws JsonProcessingException {
        // 알림 생성
        Notification notification = new Notification();
        notification.setUserId(user.getId());
        notification.setMessage(message);
        notification.setNotificationType(notificationType);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRelatedEntityId(relatedEntityId);

        notificationRepository.save(notification);

        //Redis를 통해 실시간 알림 전송
        redisTemplate.convertAndSend("notification", notification);
    }

    @Override
    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findByUserIdAndIsReadFalse(user.getId());
    }

    @Override
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow();
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public long getUnreadNotificationCount(Long userId) {
        return notificationRepository.countUnreadNotifications(userId);
    }
}
