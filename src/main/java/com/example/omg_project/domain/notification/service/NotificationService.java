package com.example.omg_project.domain.notification.service;

import com.example.omg_project.domain.notification.entity.Notification;
import com.example.omg_project.domain.user.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface NotificationService {
    void createNotification(User user, String message, String notificationType, Long relatedEntityId) throws JsonProcessingException;

    List<Notification> getUserNotifications(User user);

    void markAsRead(Long id);

    long getUnreadNotificationCount(Long userId);
}
