package com.example.omg_project.domain.user.controller;

import com.example.omg_project.domain.user.dto.request.MailRequest;
import com.example.omg_project.domain.user.dto.request.PasswordVerificationRequest;
import com.example.omg_project.domain.user.dto.request.MailVerificationRequest;
import com.example.omg_project.domain.user.service.MailService;
import com.example.omg_project.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@EnableAsync
public class MailApiController {

    private final MailService mailService;
    private final UserService userService;

    /**
     * 인증번호 발송 API
     * @param mailRequest 메일 전송 dto
     * @return 응답
     */
    @PostMapping("/api/users/mail")
    public CompletableFuture<String> mailSend(@RequestBody MailRequest mailRequest) {
        return mailService.sendMail(mailRequest.getMail())
                .thenApply(number -> String.valueOf(number));
    }

    /**
     * 인증번호 검증 API
     * @param verificationRequest 메일 검증 dto
     * @return 응답
     */
    @PostMapping("/api/users/verify-code")
    public ResponseEntity<String> verifyCode(@RequestBody MailVerificationRequest verificationRequest) {
        boolean isVerified = mailService.verifyCode(verificationRequest.getMail(), verificationRequest.getCode());
        if (isVerified) {
            return ResponseEntity.ok("Verified");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Verification failed");
        }
    }

    /**
     * 이메일 중복 체크 API
     */
    @PostMapping("/api/users/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(userService.existsByUsername(request.get("mail")));
    }

    /**
     * 닉네임 중복 체크 API
     */
    @PostMapping("/api/users/check-usernick")
    public ResponseEntity<Boolean> checkUsernick(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(userService.existsByUsernick(request.get("usernick")));
    }

    /**
     * 임시 비밀번호 재발급 발송 API
     * @param mailRequest 메일 전송 dto
     * @return 응답
     */
    @PostMapping("/api/users/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody MailRequest mailRequest) {
        String email = mailRequest.getMail();

        if (userService.existsByUsername(email)) {
            String tempPassword = mailService.createTemporaryPassword(email);
            mailService.sendTemporaryPasswordMail(email, tempPassword);
            return ResponseEntity.ok("ok");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("not found user");
        }
    }

    /**
     * 임시 비밀번호 검증 API
     * @param request 메일 전송 dto
     * @return 응답
     */
    @PostMapping("/api/users/verify-temporary-password")
    public ResponseEntity<String> verifyTemporaryPassword(@RequestBody PasswordVerificationRequest request) {
        boolean isVerified = mailService.verifyTemporaryPassword(request.getMail(), request.getTempPassword());
        if (isVerified) {
            return ResponseEntity.ok("Verified");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Verification failed");
        }
    }
}