package com.ALearning_grade2.service;

import com.ALearning_grade2.dto.UserEventDTO;

/**
 * Сервис для отправки уведомлений пользователям
 */
public interface NotificationService {
    /**
     * Отправляет уведомление на основе события пользователя
     *
     * @param userEvent DTO с данными о событии пользователя
     */
    void sendNotification(UserEventDTO userEvent);

    /**
     * Отправляет уведомление по email
     *
     * @param email email получателя
     * @param eventType тип события
     */
    void sendNotificationByEmail(String email, UserEventDTO.EventType eventType);
}