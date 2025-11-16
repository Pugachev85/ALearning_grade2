package com.ALearning_grade2.controller;

import com.ALearning_grade2.dto.UserEventDTO;
import com.ALearning_grade2.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Тестовый класс для NotificationController
 */
@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    /**
     * Тест успешной отправки уведомления
     */
    @Test
    void shouldSendNotificationSuccessfully() {
        UserEventDTO userEvent = new UserEventDTO("test@example.com", UserEventDTO.EventType.CREATED);

        ResponseEntity<String> response = notificationController.sendNotification(userEvent);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Notification sent successfully", response.getBody());
        verify(notificationService, times(1)).sendNotification(userEvent);
    }

    /**
     * Тест обработки исключения при отправке уведомления
     */
    @Test
    void shouldHandleExceptionWhenSendingNotification() {
        UserEventDTO userEvent = new UserEventDTO("test@example.com", UserEventDTO.EventType.CREATED);
        doThrow(new RuntimeException("Service unavailable")).when(notificationService).sendNotification(userEvent);

        ResponseEntity<String> response = notificationController.sendNotification(userEvent);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to send notification", response.getBody());
    }

    /**
     * Тест отправки уведомления с null данными
     */
    @Test
    void shouldHandleNullUserEvent() {
        ResponseEntity<String> response = notificationController.sendNotification(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(notificationService, times(1)).sendNotification(null);
    }
}
