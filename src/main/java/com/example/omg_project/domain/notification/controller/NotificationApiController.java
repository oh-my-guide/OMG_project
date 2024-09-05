package com.example.omg_project.domain.notification.controller;

import com.example.omg_project.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 알림 관련 API를 제공하는 컨트롤러 클래스.
 * 클라이언트로부터 알림 관련 요청을 처리하고 적절한 응답을 반환합니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationApiController {

    private final NotificationService notificationService;

    /**
     * 사용자의 읽지 않은 알림 개수를 조회하는 API 엔드포인트.
     *
     * @param userId 읽지 않은 알림 개수를 조회할 사용자의 ID
     * @return 읽지 않은 알림 개수와 함께 HTTP 상태 코드를 반환합니다.
     *         - 성공적인 경우: HTTP 200 OK와 함께 알림 개수를 반환합니다.
     *         - 실패한 경우: HTTP 500 Internal Server Error 상태 코드를 반환합니다.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getUnreadNotificationCount(@RequestParam("userId") Long userId) {
        try {
            long count = notificationService.getUnreadNotificationCount(userId);
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
