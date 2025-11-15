package com.ALearning_grade2.service;

import com.ALearning_grade2.dto.UserDTO;
import com.ALearning_grade2.exception.InvalidUserException;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс сервиса для управления пользователями.
 * Предоставляет бизнес-логику для операций с пользователями,
 * включая валидацию данных и координацию между слоями приложения.
 *
 * @author ALearning_grade2
 * @version 1.0
 */
public interface UserService {

    /**
     * Создает нового пользователя с выполнением валидации данных.
     *
     * @param userDTO объект DTO пользователя для создания, не должен быть null
     * @return созданный пользователь в виде DTO
     * @throws InvalidUserException если данные пользователя не проходят валидацию
     * @throws RuntimeException     если возникает ошибка при создании пользователя
     */
    UserDTO createUser(UserDTO userDTO) throws InvalidUserException;

    /**
     * Получает пользователя по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор пользователя
     * @return Optional содержащий пользователя в виде DTO, если найден, иначе пустой Optional
     */
    Optional<UserDTO> getUserById(Long id);

    /**
     * Получает список всех пользователей в системе.
     *
     * @return список всех пользователей в виде DTO, может быть пустым если пользователи отсутствуют
     */
    List<UserDTO> getAllUsers();

    /**
     * Обновляет информацию о существующем пользователе с выполнением валидации данных.
     *
     * @param id уникальный идентификатор пользователя для обновления
     * @param userDTO объект DTO пользователя с обновленной информацией, не должен быть null
     * @return обновленный пользователь в виде DTO
     * @throws InvalidUserException если данные пользователя не проходят валидацию
     * @throws RuntimeException     если возникает ошибка при обновлении пользователя
     */
    UserDTO updateUser(Long id, UserDTO userDTO) throws InvalidUserException;

    /**
     * Удаляет пользователя из системы по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор пользователя для удаления
     * @return true если пользователь был удален, false если пользователь не найден
     * @throws RuntimeException если возникает ошибка при удалении пользователя
     */
    boolean deleteUser(Long id);
}
