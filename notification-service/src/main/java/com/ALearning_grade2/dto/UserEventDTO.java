package com.ALearning_grade2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для передачи данных о событии пользователя
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEventDTO {
    /**
     * Email пользователя
     */
    private String email;

    /**
     * Тип события
     */
    private EventType eventType;

    /**
     * Типы событий пользователя
     */
    public enum EventType {
        /** Создание аккаунта */
        CREATED,
        /** Удаление аккаунта */
        DELETED
    }
}