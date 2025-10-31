package com.ALearning_grade2.dao;

import com.ALearning_grade2.entity.UserEntity;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для работы с сущностью User в базе данных.
 * Предоставляет CRUD операции для управления пользователями.
 */
public interface UserDao {

    /**
     * Создает нового пользователя в базе данных.
     *
     * @param user объект пользователя для создания, не должен быть null
     * @throws RuntimeException если возникает ошибка при создании пользователя
     */
    void create(UserEntity user);

    /**
     * Находит пользователя по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор пользователя
     * @return Optional содержащий пользователя, если найден, иначе пустой Optional
     * @throws RuntimeException если возникает ошибка при поиске пользователя
     */
    Optional<UserEntity> findById(Long id);

    /**
     * Получает список всех пользователей из базы данных.
     *
     * @return список всех пользователей, может быть пустым если пользователи отсутствуют
     * @throws RuntimeException если возникает ошибка при получении списка пользователей
     */
    List<UserEntity> findAll();

    /**
     * Обновляет информацию о существующем пользователе.
     *
     * @param user объект пользователя с обновленной информацией, не должен быть null
     * @throws RuntimeException если возникает ошибка при обновлении пользователя
     */
    void update(UserEntity user);

    /**
     * Удаляет пользователя из базы данных по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор пользователя для удаления
     * @throws RuntimeException если возникает ошибка при удалении пользователя
     */
    boolean delete(Long id);
}
