package com.example.omg_project.domain.notification.controller;

import com.example.omg_project.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationApiController {
    private final NotificationService notificationService;
    @GetMapping("/count")
    public ResponseEntity<Long> getUnreadNotificationCount(@RequestParam("userId") Long userId) {
        try {
            // 읽지 않은 알림 개수를 조회
            long count = notificationService.getUnreadNotificationCount(userId);

            // 결과를 ResponseEntity에 담아 반환
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            // 예외가 발생하면 상태 코드 500과 함께 에러 메시지를 반환
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
