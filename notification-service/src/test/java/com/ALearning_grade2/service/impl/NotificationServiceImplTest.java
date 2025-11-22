package com.ALearning_grade2.service.impl;

import com.ALearning_grade2.dto.UserEventDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Тестовый класс для NotificationServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private LogFakeEmailServiceImpl emailService;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    /**
     * Тест отправки уведомления для события создания
     */
    @Test
    void shouldSendNotificationForCreationEvent() {
        UserEventDTO userEvent = new UserEventDTO("test@example.com", UserEventDTO.EventType.CREATED);

        notificationService.sendNotification(userEvent);

        verify(emailService, times(1)).sendEmail(
                "test@example.com",
                "Уведомление о вашем аккаунте",
                "Здравствуйте! Ваш аккаунт на сайте был успешно создан."
        );
    }

    /**
     * Тест отправки уведомления для события удаления
     */
    @Test
    void shouldSendNotificationForDeletionEvent() {
        UserEventDTO userEvent = new UserEventDTO("user@test.com", UserEventDTO.EventType.DELETED);

        notificationService.sendNotification(userEvent);

        verify(emailService, times(1)).sendEmail(
                "user@test.com",
                "Уведомление о вашем аккаунте",
                "Здравствуйте! Ваш аккаунт был удалён."
        );
    }

    /**
     * Тест отправки уведомления по email для создания
     */
    @Test
    void shouldSendNotificationByEmailForCreation() {
        notificationService.sendNotificationByEmail("test@example.com", UserEventDTO.EventType.CREATED);

        verify(emailService, times(1)).sendEmail(
                "test@example.com",
                "Уведомление о вашем аккаунте",
                "Здравствуйте! Ваш аккаунт на сайте был успешно создан."
        );
    }

    /**
     * Тест отправки уведомления по email для удаления
     */
    @Test
    void shouldSendNotificationByEmailForDeletion() {
        notificationService.sendNotificationByEmail("user@test.com", UserEventDTO.EventType.DELETED);

        verify(emailService, times(1)).sendEmail(
                "user@test.com",
                "Уведомление о вашем аккаунте",
                "Здравствуйте! Ваш аккаунт был удалён."
        );
    }

    /**
     * Тест обработки null события
     */
    @Test
    void shouldHandleNullUserEvent() {
        notificationService.sendNotification(null);

        verify(emailService, never()).sendEmail(any(), any(), any());
    }

    /**
     * Тест обработки события с null eventType
     */
    @Test
    void shouldHandleNullEventType() {
        UserEventDTO userEvent = new UserEventDTO("test@example.com", null);

        notificationService.sendNotification(userEvent);

        verify(emailService, times(1)).sendEmail(
                "test@example.com",
                "Уведомление о вашем аккаунте",
                "Здравствуйте! Получено уведомление о вашем аккаунте."
        );
    }

    /**
     * Тест отправки уведомления с null email
     */
    @Test
    void shouldHandleNullEmailInSendNotificationByEmail() {
        notificationService.sendNotificationByEmail(null, UserEventDTO.EventType.CREATED);

        verify(emailService, never()).sendEmail(any(), any(), any());
    }

    /**
     * Тест отправки уведомления с null eventType в sendNotificationByEmail
     */
    @Test
    void shouldHandleNullEventTypeInSendNotificationByEmail() {
        notificationService.sendNotificationByEmail("test@example.com", null);

        verify(emailService, times(1)).sendEmail(
                "test@example.com",
                "Уведомление о вашем аккаунте",
                "Здравствуйте! Получено уведомление о вашем аккаунте."
        );
    }
}
