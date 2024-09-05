package com.example.omg_project.domain.notification.controller;

import com.example.omg_project.domain.notification.entity.Notification;
import com.example.omg_project.domain.notification.service.NotificationService;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.service.UserService;
import com.example.omg_project.global.jwt.util.JwtTokenizer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 알림 관련 웹 페이지와 API 요청을 처리하는 컨트롤러 클래스.
 * - 사용자의 알림을 조회하고, 알림을 읽음 상태로 변경하는 기능을 제공합니다.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtTokenizer jwtTokenizer;
    private final UserService userService;

    /**
     * 사용자의 알림 페이지를 반환하는 메서드.
     * - JWT 토큰에서 사용자 정보를 추출하고, 해당 사용자의 알림을 조회합니다.
     * - 조회된 알림을 모델에 추가하여 뷰 페이지에 전달합니다.
     *
     * @param request HTTP 요청 객체, 쿠키에서 JWT 토큰을 추출하는 데 사용됩니다.
     * @param model   Spring MVC의 모델 객체, 뷰에 전달할 데이터를 추가하는 데 사용됩니다.
     * @return 알림 페이지의 뷰 이름을 반환합니다.
     */
    @GetMapping
    public String getNotificationsPage(HttpServletRequest request, Model model) {
        // 사용자 정보 추출
        String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
        String username = jwtTokenizer.getUsernameFromToken(accessToken);
        User user = userService.findByUsername(username).orElse(null);

        if (user != null) {
            // 사용자가 존재하는 경우, 해당 사용자의 알림을 조회
            List<Notification> notifications = notificationService.getUserNotifications(user);

            model.addAttribute("user", user);
            model.addAttribute("notifications", notifications);
        }
        return "notification/notification";
    }

    /**
     * 특정 알림을 읽음 상태로 변경하는 메서드.
     * - 알림 ID를 기반으로 알림을 조회하고, 읽음 상태로 업데이트합니다.
     *
     * @param id 읽음 상태로 변경할 알림의 ID
     * @return HTTP 상태 코드 200 OK를 반환합니다.
     */
    @PostMapping("/{id}/read")
    @ResponseBody
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}
