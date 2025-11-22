package com.ALearning_grade2.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO для передачи данных о событии пользователя
 */
@Getter
@Setter
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