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

    @PostMapping("/api/mail")
    public CompletableFuture<String> mailSend(@RequestBody MailRequest mailRequest) {
        return mailService.sendMail(mailRequest.getMail())
                .thenApply(number -> String.valueOf(number));
    }

    @PostMapping("/api/verify-code")
    public String verifyCode(@RequestBody VerificationRequest verificationRequest) {
        boolean isVerified = mailService.verifyCode(verificationRequest.getMail(), verificationRequest.getCode());
        return isVerified ? "Verified" : "Verification failed";
    }

    @PostMapping("/api/check-email")
    public ResponseEntity<String> checkEmail(@RequestBody Map<String, String> request) {
        String email = request.get("mail");
        Optional<User> existingUser = userServiceimpl.findByUsername(email);

        if (existingUser.isPresent()) {
            return ResponseEntity.ok("아이디가 이미 존재합니다."); // 이메일이 이미 존재함
        } else {
            return ResponseEntity.ok("사용 가능한 아이디입니다."); // 이메일 사용 가능
        }
    }

    @PostMapping("/api/check-usernick")
    public ResponseEntity<Boolean> checkUsername(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(userServiceimpl.existsByUsernick(request.get("usernick")));
    }
}