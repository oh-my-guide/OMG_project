package com.example.omg_project.domain.user.service.impl;

import com.example.omg_project.domain.user.service.MailService;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.repository.UserRepository;
import com.example.omg_project.global.exception.CustomException;
import com.example.omg_project.global.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender; // JAVAMailSender 를 주입받아 이메일 전송 가능
    private static final String senderEmail = "ch9800113@gmail.com";
    private static final Map<String, Integer> verificationCodes = new HashMap<>();

    /**
     * 회원 가입시 인증 코드 자동 생성 메서드
     * @param email 이메일 주소
     */
    private static void createNumber(String email){
        SecureRandom random = new SecureRandom();
        int num = random.nextInt(1000000);
        verificationCodes.put(email, num);
    }

    /**
     * 이메일 전송
     * @param mail 이메일 주소
     */
    @Override
    public MimeMessage createMail(String mail){
        createNumber(mail);
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(senderEmail);
            helper.setTo(mail);
            helper.setSubject("OMG 회원가입 인증번호");
            String body = "<h2>OMG에 오신걸 환영합니다!</h2><h3>아래의 인증번호를 입력하세요.</h3><h1>" + verificationCodes.get(mail) + "</h1><h3>감사합니다.</h3>";
            helper.setText(body, true);
        } catch (MessagingException e) {
            throw new CustomException(ErrorCode.EMAIL_CREATION_ERROR);
        }

        return message;
    }

    /**
     * createMail() 메서드의 내용을 바탕으로 이메일 전송
     * @param mail 이메일 주소
     * @return 응답
     */
    @Async
    @Override
    public CompletableFuture<Integer> sendMail(String mail) {
        MimeMessage message = createMail(mail);
        javaMailSender.send(message);
        return CompletableFuture.completedFuture(verificationCodes.get(mail));
    }

    /**
     * 이메일 인증 코드 검증
     * @param mail 이메일 주소
     * @param code 인증 코드
     * @return 응답
     */
    @Override
    public boolean verifyCode(String mail, int code) {
        Integer storedCode = verificationCodes.get(mail);
        return storedCode != null && storedCode == code;
    }

    /**
     * 임시 비밀번호 자동 생성 메서드
     * @return 응답
     */
    private static String generateRandomPassword() {
        int length = 8;
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    /**
     * 임시 비밀번호 메일 내용 작성
     * @param mail 이메일 주소
     * @param tempPassword 임시 비밀번호 코드
     */
    @Override
    public void sendTemporaryPasswordMail(String mail, String tempPassword) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(senderEmail);
            helper.setTo(mail);
            helper.setSubject("OMG 임시 비밀번호");
            String body = "<h2>OMG에 오신걸 환영합니다!</h2><p>아래의 임시 비밀번호를 사용하세요.</p><h1>" + tempPassword + "</h1><h3>로그인 후 반드시 비밀번호를 재설정하세요.</h3>";
            helper.setText(body, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new CustomException(ErrorCode.TEMP_PASSWORD_SENDING_ERROR);
        }
    }

    /**
     * 임시 비밀번호 생성 및 DB 업데이트
     * @param mail 이메일 주소
     * @return 응답
     */
    @Override
    public String createTemporaryPassword(String mail) {
        String tempPassword = generateRandomPassword();
        User user = userRepository.findByUsername(mail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);
        return tempPassword;
    }

    /**
     * 임시 비밀번호 검증
     * @param mail 이메일 즈소
     * @param tempPassword 임시 비밀번호 코드
     * @return 응답
     */
    @Override
    public boolean verifyTemporaryPassword(String mail, String tempPassword) {
        User user = userRepository.findByUsername(mail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return passwordEncoder.matches(tempPassword, user.getPassword());
    }
}