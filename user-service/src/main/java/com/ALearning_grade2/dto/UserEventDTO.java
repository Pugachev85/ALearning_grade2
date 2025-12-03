package com.ALearning_grade2.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO для передачи событий пользователя через Kafka
 */
@Getter
@Setter
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
