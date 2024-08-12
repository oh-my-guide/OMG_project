package com.example.omg_project.domain.user.service;

import jakarta.mail.internet.MimeMessage;
import java.util.concurrent.CompletableFuture;

public interface MailService {

    MimeMessage createMail(String mail);

    boolean verifyCode(String email, int code);

    CompletableFuture<Integer> sendMail(String mail);

    String createTemporaryPassword(String email);

    boolean verifyTemporaryPassword(String email, String tempPassword);

    void sendTemporaryPasswordMail(String email, String tempPassword);
}