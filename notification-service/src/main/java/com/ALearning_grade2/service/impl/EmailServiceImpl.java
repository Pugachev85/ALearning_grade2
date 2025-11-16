package com.ALearning_grade2.service.impl;

import com.ALearning_grade2.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Реализация сервиса отправки email (заглушка для демонстрации)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    //    private final JavaMailSender mailSender;

    /**
     * {@inheritDoc}
     * Реализация-заглушка для демонстрации (логирует вместо реальной отправки)
     */
    @Override
    public void sendEmail(String to, String subject, String text) {

//        try {
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setTo(to);
//            message.setSubject(subject);
//            message.setText(text);
//
//            mailSender.send(message);
//            log.info("Email sent successfully to: {}", to);
//        } catch (Exception e) {
//            log.error("Failed to send email to: {}", to, e);
//            throw new RuntimeException("Failed to send email", e);
//        }

        log.info("EMAIL SENT - To: {}, Subject: {}, Text: {}", to, subject, text);
        log.info("In production this would send a real email to: {}", to);
    }
}