package com.example.omg_project.domain.certification.controller;

import com.example.omg_project.domain.certification.entity.MailRequest;
import com.example.omg_project.domain.certification.entity.VerificationRequest;
import com.example.omg_project.domain.certification.service.MailService;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@EnableAsync
public class MailController {

    private final MailService mailService;
    private final UserServiceImpl userServiceimpl;

    /**
     * 이메일 전송 메서드
     */
    @PostMapping("/api/mail")
    public CompletableFuture<String> mailSend(@RequestBody MailRequest mailRequest) {
        return mailService.sendMail(mailRequest.getMail())
                .thenApply(number -> String.valueOf(number));
    }

    /**
     * 인증 코드 검증 메서드
     */
    @PostMapping("/api/verify-code")
    public String verifyCode(@RequestBody VerificationRequest verificationRequest) {
        boolean isVerified = mailService.verifyCode(verificationRequest.getMail(), verificationRequest.getCode());
        return isVerified ? "Verified" : "Verification failed";
    }

    /**
     * 이메일 중복 체크 메서드
     */
    @PostMapping("/api/check-email")
    public ResponseEntity<String> checkEmail(@RequestBody Map<String, String> request) {
        String email = request.get("mail");
        Optional<User> existingUser = userServiceimpl.findByUsername(email);

        if (existingUser.isPresent()) {
            return ResponseEntity.ok("아이디가 이미 존재합니다.");
        } else {
            return ResponseEntity.ok("사용 가능한 아이디입니다.");
        }
    }

    /**
     * 닉네임 중복 체크 메서드
     */
    @PostMapping("/api/check-usernick")
    public ResponseEntity<Boolean> checkUsername(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(userServiceimpl.existsByUsernick(request.get("usernick")));
    }
}