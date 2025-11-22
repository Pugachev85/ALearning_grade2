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
public class LogFakeEmailServiceImpl implements EmailService {

    /**
     * {@inheritDoc}
     * Реализация-заглушка для демонстрации (логирует вместо реальной отправки)
     */
    @Override
    public void sendEmail(String to, String subject, String text) {

        log.info("EMAIL SENT - To: {}, Subject: {}, Text: {}", to, subject, text);
        log.info("In production this would send a real email to: {}", to);
    }
}