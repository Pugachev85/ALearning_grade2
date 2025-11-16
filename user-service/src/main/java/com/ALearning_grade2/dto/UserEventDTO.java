package com.ALearning_grade2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO для передачи событий пользователя через Kafka
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEventDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String email;
    private EventType eventType;

    public enum EventType {
        CREATED, DELETED
    }
}
