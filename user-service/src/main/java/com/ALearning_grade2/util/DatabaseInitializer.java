package com.ALearning_grade2.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Компонент для проверки состояния базы данных после запуска приложения.
 * Выполняет базовые проверки подключения и структуры БД.
 */
@Component
@Slf4j
public class DatabaseInitializer {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param jdbcTemplate шаблон JdbcTemplate для работы с БД
     */
    public DatabaseInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Обработчик события готовности приложения.
     * Выполняет проверку базы данных после успешного запуска.
     *
     * @param event событие готовности приложения
     */
    @EventListener(ApplicationReadyEvent.class)
    public void checkDatabaseStatus(ApplicationReadyEvent event) {
        log.info("Проверка состояния базы данных после запуска приложения");

        try {
            // Проверяем существование таблицы users
            Integer userCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'users'",
                    Integer.class
            );

            if (userCount != null && userCount > 0) {
                log.info("Таблица 'users' найдена в базе данных");

                // Проверяем количество записей
                Integer recordCount = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM users",
                        Integer.class
                );

                log.info("Количество пользователей в базе данных: {}", recordCount);
            } else {
                log.warn("Таблица 'users' не найдена в базе данных");
            }

        } catch (Exception e) {
            log.error("Ошибка при проверке состояния базы данных: {}", e.getMessage());
        }
    }
}
