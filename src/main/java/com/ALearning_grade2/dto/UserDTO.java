package com.ALearning_grade2.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object для сущности пользователя.
 * Используется для передачи данных между слоями приложения
 * и валидации входящих данных.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    /**
     * Уникальный идентификатор пользователя
     */
    private Long id;

    /**
     * Имя пользователя. Не может быть пустым
     */
    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    /**
     * Электронная почта пользователя. Должна быть валидным email адресом
     */
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;

    /**
     * Возраст пользователя. Должен быть в диапазоне 0-150 лет
     */
    @NotNull(message = "Возраст не может быть null")
    @Min(value = 0, message = "Возраст должен быть не менее 0")
    @Max(value = 150, message = "Возраст должен быть не более 150")
    private Integer age;

    /**
     * Дата и время создания пользователя
     */
    private LocalDateTime createdAt;
}
