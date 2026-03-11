package com.davidmoura.secureauth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Async
    public void sendVerificationEmail(String to, String token) {
        String link = "http://localhost:8080/api/v1/auth/verify-email?token=" + token;

        log.info("\n======================================================");
        log.info("MOCK EMAIL SENDER");
        log.info("To: {}", to);
        log.info("Subject: Please verify your email address");
        log.info("Link: {}", link);
        log.info("======================================================\n");
    }

    @Async
    public void sendPasswordResetEmail(String to, String token) {
        String link = "http://localhost:8080/api/v1/auth/reset-password?token=" + token;

        log.info("\n======================================================");
        log.info("MOCK EMAIL SENDER - PASSWORD RESET");
        log.info("To: {}", to);
        log.info("Subject: Password Reset Request");
        log.info("Link: {}", link);
        log.info("Note: This link expires in 15 minutes.");
        log.info("======================================================\n");
    }
}