package com.ALearning_grade2.controller;

import com.ALearning_grade2.dto.UserEventDTO;
import com.ALearning_grade2.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для обработки запросов на отправку уведомлений
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Отправляет уведомление на основе данных о событии пользователя
     *
     * @param userEvent DTO с данными о событии пользователя
     * @return ResponseEntity с результатом операции
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody UserEventDTO userEvent) {
        try {
            notificationService.sendNotification(userEvent);
            return ResponseEntity.ok("Notification sent successfully");
        } catch (Exception e) {
            log.error("Error sending notification", e);
            return ResponseEntity.internalServerError().body("Failed to send notification");
        }
    }
}