package com.ALearning_grade2.service.impl;

import com.ALearning_grade2.dto.UserEventDTO;
import com.ALearning_grade2.service.EmailService;
import com.ALearning_grade2.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Реализация сервиса уведомлений
 */
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final EmailService emailService;

    public NotificationServiceImpl(@Qualifier("logFakeEmailServiceImpl") EmailService emailService) {
        this.emailService = emailService;
    } //EmailService заменен на реализацию-заглушку для демонстрации без реальной отправки email

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendNotification(UserEventDTO userEvent) {
        if (userEvent == null) {
            log.warn("Attempted to send notification for null user event");
            return;
        }

        String subject = "Уведомление о вашем аккаунте";
        String text = generateMessageText(userEvent.getEventType());

        emailService.sendEmail(userEvent.getEmail(), subject, text);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendNotificationByEmail(String email, UserEventDTO.EventType eventType) {
        if (email == null) {
            log.warn("Attempted to send notification to null email");
            return;
        }

        String subject = "Уведомление о вашем аккаунте";
        String text = generateMessageText(eventType);

        emailService.sendEmail(email, subject, text);
    }

    /**
     * Генерирует текст сообщения в зависимости от типа события
     *
     * @param eventType тип события
     * @return текст сообщения
     */
    private String generateMessageText(UserEventDTO.EventType eventType) {
        if (eventType == null) {
            return "Здравствуйте! Получено уведомление о вашем аккаунте.";
        }

        return switch (eventType) {
            case CREATED -> "Здравствуйте! Ваш аккаунт на сайте был успешно создан.";
            case DELETED -> "Здравствуйте! Ваш аккаунт был удалён.";
            default -> "Здравствуйте! Уведомление о вашем аккаунте.";
        };
    }
}
