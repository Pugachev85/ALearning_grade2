package com.ALearning_grade2.service.impl;

import com.ALearning_grade2.dao.UserDao;
import com.ALearning_grade2.entity.UserEntity;
import com.ALearning_grade2.exception.InvalidUserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Модульные тесты для UserServiceImpl.
 * Проверяет корректность работы сервиса пользователей с использованием Mockito для мокирования зависимостей.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl userService;

    /**
     * Тест создания пользователя с валидными данными.
     * Проверяет, что метод DAO вызывается корректно.
     */
    @Test
    void createUserWithValidUserShouldCallDaoCreate() throws InvalidUserException {
        // Given
        UserEntity user = createUserEntity("John Doe", "john@example.com", 25);

        // When
        userService.createUser(user);

        // Then
        verify(userDao).create(user);
    }

    /**
     * Тест создания пользователя с null именем.
     * Проверяет выбрасывание исключения InvalidUserException.
     */
    @Test
    void createUserWithNullNameShouldThrowInvalidUserException() {
        // Given
        UserEntity user = createUserEntity(null, "john@example.com", 25);

        // When & Then
        InvalidUserException exception = assertThrows(InvalidUserException.class,
                () -> userService.createUser(user),
                "Должно быть выброшено исключение при null имени");

        assertEquals("Имя пользователя не может быть пустым", exception.getMessage(),
                "Сообщение об ошибке должно соответствовать ожидаемому");
        verify(userDao, never()).create(any());
    }

    /**
     * Тест создания пользователя с пустым именем.
     * Проверяет выбрасывание исключения InvalidUserException.
     */
    @Test
    void createUserWithEmptyNameShouldThrowInvalidUserException() {
        // Given
        UserEntity user = createUserEntity("", "john@example.com", 25);

        // When & Then
        InvalidUserException exception = assertThrows(InvalidUserException.class,
                () -> userService.createUser(user),
                "Должно быть выброшено исключение при пустом имени");

        assertEquals("Имя пользователя не может быть пустым", exception.getMessage(),
                "Сообщение об ошибке должно соответствовать ожидаемому");
        verify(userDao, never()).create(any());
    }

    /**
     * Тест создания пользователя с null email.
     * Проверяет выбрасывание исключения InvalidUserException.
     */
    @Test
    void createUserWithNullEmailShouldThrowInvalidUserException() {
        // Given
        UserEntity user = createUserEntity("John Doe", null, 25);

        // When & Then
        InvalidUserException exception = assertThrows(InvalidUserException.class,
                () -> userService.createUser(user),
                "Должно быть выброшено исключение при null email");

        assertEquals("Email не может быть пустым", exception.getMessage(),
                "Сообщение об ошибке должно соответствовать ожидаемому");
        verify(userDao, never()).create(any());
    }

    /**
     * Тест создания пользователя с некорректным форматом email.
     * Проверяет выбрасывание исключения InvalidUserException.
     */
    @Test
    void createUserWithInvalidEmailFormatShouldThrowInvalidUserException() {
        // Given
        UserEntity user = createUserEntity("John Doe", "invalid-email", 25);

        // When & Then
        InvalidUserException exception = assertThrows(InvalidUserException.class,
                () -> userService.createUser(user),
                "Должно быть выброшено исключение при некорректном формате email");

        assertEquals("Некорректный формат email", exception.getMessage(),
                "Сообщение об ошибке должно соответствовать ожидаемому");
        verify(userDao, never()).create(any());
    }

    /**
     * Тест создания пользователя с отрицательным возрастом.
     * Проверяет выбрасывание исключения InvalidUserException.
     */
    @Test
    void createUserWithNegativeAgeShouldThrowInvalidUserException() {
        // Given
        UserEntity user = createUserEntity("John Doe", "john@example.com", -1);

        // When & Then
        InvalidUserException exception = assertThrows(InvalidUserException.class,
                () -> userService.createUser(user),
                "Должно быть выброшено исключение при отрицательном возрасте");

        assertEquals("Возраст должен быть в диапазоне 0–150", exception.getMessage(),
                "Сообщение об ошибке должно соответствовать ожидаемому");
        verify(userDao, never()).create(any());
    }

    /**
     * Тест создания пользователя с возрастом больше 150.
     * Проверяет выбрасывание исключения InvalidUserException.
     */
    @Test
    void createUserWithAgeOver150ShouldThrowInvalidUserException() {
        // Given
        UserEntity user = createUserEntity("John Doe", "john@example.com", 151);

        // When & Then
        InvalidUserException exception = assertThrows(InvalidUserException.class,
                () -> userService.createUser(user),
                "Должно быть выброшено исключение при возрасте больше 150");

        assertEquals("Возраст должен быть в диапазоне 0–150", exception.getMessage(),
                "Сообщение об ошибке должно соответствовать ожидаемому");
        verify(userDao, never()).create(any());
    }

    /**
     * Тест получения пользователя по существующему ID.
     * Проверяет возврат корректного пользователя.
     */
    @Test
    void getUserByIdWithExistingUserShouldReturnUser() {
        // Given
        Long userId = 1L;
        UserEntity expectedUser = createUserEntity("John Doe", "john@example.com", 25);
        when(userDao.findById(userId)).thenReturn(Optional.of(expectedUser));

        // When
        Optional<UserEntity> result = userService.getUserById(userId);

        // Then
        assertTrue(result.isPresent(), "Пользователь должен быть найден");
        assertEquals(expectedUser, result.get(), "Возвращенный пользователь должен совпадать с ожидаемым");
        verify(userDao).findById(userId);
    }

    /**
     * Тест получения пользователя по несуществующему ID.
     * Проверяет возврат пустого Optional.
     */
    @Test
    void getUserByIdWithNonExistingUserShouldReturnEmpty() {
        // Given
        Long userId = 1L;
        when(userDao.findById(userId)).thenReturn(Optional.empty());

        // When
        Optional<UserEntity> result = userService.getUserById(userId);

        // Then
        assertFalse(result.isPresent(), "Результат должен быть пустым для несуществующего пользователя");
        verify(userDao).findById(userId);
    }

    /**
     * Тест получения всех пользователей при их наличии.
     * Проверяет возврат полного списка пользователей.
     */
    @Test
    void getAllUsersWhenUsersExistShouldReturnListOfUsers() {
        // Given
        UserEntity user1 = createUserEntity("John Doe", "john@example.com", 25);
        UserEntity user2 = createUserEntity("Jane Smith", "jane@example.com", 30);
        List<UserEntity> expectedUsers = Arrays.asList(user1, user2);
        when(userDao.findAll()).thenReturn(expectedUsers);

        // When
        List<UserEntity> result = userService.getAllUsers();

        // Then
        assertEquals(expectedUsers, result, "Список пользователей должен совпадать с ожидаемым");
        assertEquals(2, result.size(), "Должно быть 2 пользователя в списке");
        verify(userDao).findAll();
    }

    /**
     * Тест получения всех пользователей при их отсутствии.
     * Проверяет возврат пустого списка.
     */
    @Test
    void getAllUsersWhenNoUsersShouldReturnEmptyList() {
        // Given
        when(userDao.findAll()).thenReturn(List.of());

        // When
        List<UserEntity> result = userService.getAllUsers();

        // Then
        assertTrue(result.isEmpty(), "Список должен быть пустым при отсутствии пользователей");
        verify(userDao).findAll();
    }

    /**
     * Тест обновления пользователя с валидными данными.
     * Проверяет вызов метода обновления в DAO.
     */
    @Test
    void updateUserWithValidUserShouldCallDaoUpdate() throws InvalidUserException {
        // Given
        UserEntity user = createUserEntity("John Doe", "john@example.com", 25);

        // When
        userService.updateUser(user);

        // Then
        verify(userDao).update(user);
    }

    /**
     * Тест обновления пользователя с невалидными данными.
     * Проверяет выбрасывание исключения и отсутствие вызова DAO.
     */
    @Test
    void updateUserWithInvalidUserShouldThrowExceptionAndNotCallDao() {
        // Given
        UserEntity user = createUserEntity("", "john@example.com", 25);

        // When & Then
        assertThrows(InvalidUserException.class,
                () -> userService.updateUser(user),
                "Должно быть выброшено исключение при невалидном пользователе");
        verify(userDao, never()).update(any());
    }

    /**
     * Тест успешного удаления пользователя.
     * Проверяет возврат true при успешном удалении.
     */
    @Test
    void deleteUserWhenSuccessfulShouldReturnTrue() {
        // Given
        Long userId = 1L;
        when(userDao.delete(userId)).thenReturn(true);

        // When
        boolean result = userService.deleteUser(userId);

        // Then
        assertTrue(result, "Удаление должно завершиться успешно");
        verify(userDao).delete(userId);
    }

    /**
     * Тест неуспешного удаления пользователя.
     * Проверяет возврат false при неудачном удалении.
     */
    @Test
    void deleteUserWhenUnsuccessfulShouldReturnFalse() {
        // Given
        Long userId = 1L;
        when(userDao.delete(userId)).thenReturn(false);

        // When
        boolean result = userService.deleteUser(userId);

        // Then
        assertFalse(result, "Удаление должно завершиться неудачей");
        verify(userDao).delete(userId);
    }

    /**
     * Вспомогательный метод для создания тестового объекта UserEntity.
     *
     * @param name имя пользователя
     * @param email email пользователя
     * @param age возраст пользователя
     * @return созданный объект UserEntity
     */
    private UserEntity createUserEntity(String name, String email, int age) {
        UserEntity user = new UserEntity();
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);
        return user;
    }
}
