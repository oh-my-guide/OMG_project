package com.example.omg_project.domain.certification.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private static final String senderEmail = "OMG@gmail.com";
    private static final Map<String, Integer> verificationCodes = new HashMap<>();

    /**
     * 인증 코드 자동 생성 메서드
     */
    public static void createNumber(String email){
        int number = new Random().nextInt(900000) + 100000; // 100000-999999 사이의 숫자 생성
        verificationCodes.put(email, number);
    }

    /**
     * 이메일 생성 메서드
     */
    public MimeMessage createMail(String mail){
        createNumber(mail);
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(senderEmail);
            helper.setTo(mail);
            helper.setSubject("이메일 인증");
            String body = "<h2>OMG에 오신걸 환영합니다!</h2><h3>아래의 인증번호를 입력하세요.</h3><h1>" + verificationCodes.get(mail) + "</h1><h3>감사합니다.</h3>";
            helper.setText(body, true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return message;
    }

    /**
     * createMail() 메서드의 내용을 이메일로 전송 메서드
     */
    @Async
    public CompletableFuture<Integer> sendMail(String mail) {
        MimeMessage message = createMail(mail);
        javaMailSender.send(message);
        return CompletableFuture.completedFuture(verificationCodes.get(mail));
    }

    /**
     * 인증 코드 검증 메서드
     */
    public boolean verifyCode(String email, int code) {
        Integer storedCode = verificationCodes.get(email);
        return storedCode != null && storedCode == code;
    }
}