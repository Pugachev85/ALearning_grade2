package com.ALearning_grade2.service.impl;

import com.ALearning_grade2.entity.UserEntity;
import com.ALearning_grade2.repository.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Сервис доступа к базе данных пользователей.
 * <p>
 * Предоставляет методы для получения, поиска и сохранения сущностей
 * {@link UserEntity}. С помощью Resilience4j реализованы
 * <i>circuit‑breaker</i> и <i>retry</i>‑механизмы, а также fallback‑методы,
 * которые срабатывают при недоступности БД.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseService {

    private final UserRepository userRepository;

    /**
     * Возвращает всех пользователей из БД.
     *
     * @return список всех {@link UserEntity}; при сбое – пустой список
     */
    @CircuitBreaker(name = "databaseService", fallbackMethod = "fallbackFindAll")
    @Retry(name = "databaseRetry")
    public List<UserEntity> findAllUsers() {
        log.info("Попытка получить всех пользователей из базы данных");
        return userRepository.findAll();
    }

    /**
     * Находит пользователя по идентификатору.
     *
     * @param id идентификатор искомого пользователя
     * @return {@link Optional} с найденным {@link UserEntity},
     * либо {@link Optional#empty()} при ошибке
     */
    @CircuitBreaker(name = "databaseService", fallbackMethod = "fallbackFindById")
    @Retry(name = "databaseRetry")
    public Optional<UserEntity> findUserById(Long id) {
        log.info("Попытка найти пользователя по ID: {}", id);
        return userRepository.findById(id);
    }

    /**
     * Сохраняет (создаёт/обновляет) пользователя в БД.
     *
     * @param user сущность пользователя для сохранения
     * @return сохранённый объект {@link UserEntity}
     */
    @CircuitBreaker(name = "databaseService", fallbackMethod = "fallbackSave")
    @Retry(name = "databaseRetry")
    public UserEntity saveUser(UserEntity user) {
        log.info("Попытка сохранить пользователя: {}", user.getEmail());
        return userRepository.save(user);
    }

    /**
     * Резервный метод для {@link #findAllUsers()}.
     *
     * @param ex исключение, вызвавшее fallback
     * @return пустой список, чтобы вызывающий код мог безопасно продолжить работу
     */
    public List<UserEntity> fallbackFindAll(Exception ex) {
        log.warn("Fallback сработал для findAllUsers из‑за: {}", ex.getMessage());
        return Collections.emptyList();
    }

    /**
     * Резервный метод для {@link #findUserById(Long)}.
     *
     * @param id идентификатор, который пытались найти
     * @param ex исключение, вызвавшее fallback
     * @return {@link Optional#empty()} – сигнализирует об отсутствии результата
     */
    public Optional<UserEntity> fallbackFindById(Long id, Exception ex) {
        log.warn("Fallback сработал для findUserById с ID {} из‑за: {}", id, ex.getMessage());
        return Optional.empty();
    }

    /**
     * Резервный метод для {@link #saveUser(UserEntity)}.
     *
     * @param user пользователь, который не удалось сохранить
     * @param ex   исключение, вызвавшее fallback
     * @return никогда не возвращает (выбрасывает {@link RuntimeException})
     */
    public UserEntity fallbackSave(UserEntity user, Exception ex) {
        log.error("Fallback сработал для saveUser. Пользователь {} не сохранён из‑за: {}",
                user.getEmail(), ex.getMessage());
        throw new RuntimeException("База данных недоступна – пользователь не сохранён");
    }
}
