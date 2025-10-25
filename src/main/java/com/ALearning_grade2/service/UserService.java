package com.ALearning_grade2.service;

import com.ALearning_grade2.entity.User;
import com.ALearning_grade2.exception.InvalidUserException;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс сервиса для управления пользователями.
 * Предоставляет бизнес-логику для операций с пользователями,
 * включая валидацию данных и координацию между слоями приложения.
 */
public interface UserService {

    /**
     * Создает нового пользователя с выполнением валидации данных.
     *
     * @param user объект пользователя для создания, не должен быть null
     * @throws InvalidUserException если данные пользователя не проходят валидацию
     * @throws RuntimeException     если возникает ошибка при создании пользователя
     */
    void createUser(User user) throws InvalidUserException;

    /**
     * Получает пользователя по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор пользователя
     * @return Optional содержащий пользователя, если найден, иначе пустой Optional
     */
    Optional<User> getUserById(Long id);

    /**
     * Получает список всех пользователей в системе.
     *
     * @return список всех пользователей, может быть пустым если пользователи отсутствуют
     */
    List<User> getAllUsers();

    /**
     * Обновляет информацию о существующем пользователе с выполнением валидации данных.
     *
     * @param user объект пользователя с обновленной информацией, не должен быть null
     * @throws InvalidUserException если данные пользователя не проходят валидацию
     * @throws RuntimeException     если возникает ошибка при обновлении пользователя
     */
    void updateUser(User user) throws InvalidUserException;

    /**
     * Удаляет пользователя из системы по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор пользователя для удаления
     * @throws RuntimeException если возникает ошибка при удалении пользователя
     */
    boolean deleteUser(Long id);
}
