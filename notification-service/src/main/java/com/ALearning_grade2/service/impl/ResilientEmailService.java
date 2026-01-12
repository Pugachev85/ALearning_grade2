package com.ALearning_grade2.service.impl;

import com.ALearning_grade2.service.EmailService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Обёртка над {@link EmailService}, которая добавляет
 * устойчивость к отказам при отправке писем.
 *
 * <p>С помощью {@code @CircuitBreaker} и {@code @Retry}
 * реализуются механизмы открытого/закрытого автоматического
 * переключателя и повторных попыток отправки. При окончательном
 * сбое вызывается метод {@link #fallbackSendEmail(String, String, String, Exception)}.</p>
 */
@Service
@Slf4j
public class ResilientEmailService {

    /**
     * Реальная реализация отправки письма (заглушка или настоящий сервис).
     */
    private final EmailService emailService;

    public ResilientEmailService(@Qualifier("logFakeEmailServiceImpl") EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Отправка письма с использованием Resilience4j.
     *
     * @param to      адрес получателя
     * @param subject тема письма
     * @param text    тело письма
     */
    @CircuitBreaker(name = "emailService", fallbackMethod = "fallbackSendEmail")
    @Retry(name = "emailRetry", fallbackMethod = "fallbackSendEmail")
    public void sendEmailWithResilience(String to, String subject, String text) {
        log.info("Попытка отправить письмо на адрес: {}", to);
        emailService.sendEmail(to, subject, text);
        log.info("Письмо успешно отправлено на адрес: {}", to);
    }

    /**
     * Метод‑запас при сбое отправки.
     *
     * @param to      адрес получателя
     * @param subject тема письма
     * @param text    тело письма
     * @param ex      исключение, вызвавшее сбой
     */
    public void fallbackSendEmail(String to, String subject, String text, Exception ex) {
        log.error("Сработал fallback при отправке письма на {}. Ошибка: {}", to, ex.getMessage());
        // Здесь обычно сохраняем событие в очередь/БД для последующей переотправки
        log.warn("Письмо на {} помещено в очередь повторной отправки. Тема: {}", to, subject);
    }
}
