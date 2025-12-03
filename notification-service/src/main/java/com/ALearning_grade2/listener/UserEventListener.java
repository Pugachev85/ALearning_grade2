package com.ALearning_grade2.listener;

import com.ALearning_grade2.dto.UserEventDTO;
import com.ALearning_grade2.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Слушатель событий пользователя из Kafka
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventListener {

    private final NotificationService notificationService;

    /**
     * Обрабатывает события пользователя из Kafka
     *
     * @param userEvent DTO с данными о событии пользователя
     */
    @KafkaListener(topics = "user-events", groupId = "notification-group")
    public void handleUserEvent(UserEventDTO userEvent) {
        log.info("Received user event: {} for email: {}", userEvent.getEventType(), userEvent.getEmail());
        notificationService.sendNotification(userEvent);
    }
}