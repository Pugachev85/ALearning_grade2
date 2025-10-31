package com.ALearning_grade2.dao;

import com.ALearning_grade2.entity.UserEntity;
import com.ALearning_grade2.exception.UserServiceException;
import com.ALearning_grade2.factory.HibernateFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Интеграционные тесты для UserDaoImpl с использованием Testcontainers.
 * Тестирует взаимодействие с реальной базой данных PostgreSQL.
 */
@Testcontainers
class UserDaoImplTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.5")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    private UserDao userDao;

    /**
     * Настраивает контейнер PostgreSQL перед выполнением всех тестов.
     */
    @BeforeAll
    static void setUpContainer() {
        // Отключаем Liquibase для тестов
        System.setProperty("liquibase.enabled", "false");
        System.setProperty("hibernate.liquibase.enabled", "false");

        // Настройки Hibernate для тестов
        System.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        System.setProperty("hibernate.show_sql", "false");
        System.setProperty("hibernate.format_sql", "false");
    }

    @AfterAll
    static void tearDown() {
        // Очищаем свойства
        System.clearProperty("liquibase.enabled");
        System.clearProperty("hibernate.liquibase.enabled");
        System.clearProperty("hibernate.hbm2ddl.auto");
        System.clearProperty("hibernate.show_sql");
        System.clearProperty("hibernate.format_sql");

        HibernateFactory.shutdown();
    }

    /**
     * Инициализирует UserDao перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        userDao = new UserDaoImpl();
        clearUsersTable();
    }

    /**
     * Очищает таблицу users после каждого теста.
     */
    @AfterEach
    void tearDownAfterTest() {
        clearUsersTable();
    }

    /**
     * Очищает таблицу users для изоляции тестов.
     */
    private void clearUsersTable() {
        Transaction tx = null;
        try (Session session = HibernateFactory.openSession()) {
            tx = session.beginTransaction();
            session.createMutationQuery("DELETE FROM UserEntity").executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
        }
    }

    /**
     * Создает тестового пользователя с уникальным email.
     *
     * @return созданный объект UserEntity
     */
    private UserEntity createTestUser() {
        UserEntity user = new UserEntity();
        user.setName("Test User " + UUID.randomUUID().toString().substring(0, 8));
        user.setEmail("test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com");
        user.setAge(25);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    /**
     * Тестирует создание пользователя с валидными данными.
     * Ожидается, что пользователь будет сохранен в базе данных с присвоенным ID.
     */
    @Test
    void createWithValidUserShouldCreateUserInDatabase() {
        // Given
        UserEntity testUser = createTestUser();

        // When
        userDao.create(testUser);

        // Then
        assertNotNull(testUser.getId(), "ID пользователя должен быть присвоен после создания");
        assertTrue(testUser.getId() > 0, "ID должен быть положительным числом");

        Optional<UserEntity> foundUser = userDao.findById(testUser.getId());
        assertTrue(foundUser.isPresent(), "Пользователь должен существовать в базе данных");
        assertEquals(testUser.getName(), foundUser.get().getName(), "Имя пользователя должно совпадать");
        assertEquals(testUser.getEmail(), foundUser.get().getEmail(), "Email пользователя должен совпадать");
        assertEquals(testUser.getAge(), foundUser.get().getAge(), "Возраст пользователя должен совпадать");
    }

    /**
     * Тестирует поиск пользователя по-существующему ID.
     * Ожидается возврат пользователя с корректными данными.
     */
    @Test
    void findByIdWithExistingIdShouldReturnUser() {
        // Given
        UserEntity testUser = createTestUser();
        userDao.create(testUser);

        // When
        Optional<UserEntity> result = userDao.findById(testUser.getId());

        // Then
        assertTrue(result.isPresent(), "Пользователь должен быть найден по существующему ID");
        assertEquals(testUser.getId(), result.get().getId(), "ID пользователя должен совпадать");
        assertEquals(testUser.getName(), result.get().getName(), "Имя пользователя должно совпадать");
        assertEquals(testUser.getEmail(), result.get().getEmail(), "Email пользователя должен совпадать");
        assertEquals(testUser.getAge(), result.get().getAge(), "Возраст пользователя должен совпадать");
    }

    /**
     * Тестирует поиск пользователя по-несуществующему ID.
     * Ожидается возврат пустого Optional.
     */
    @Test
    void findByIdWithNonExistingIdShouldReturnEmpty() {
        // When
        Optional<UserEntity> result = userDao.findById(999L);

        // Then
        assertFalse(result.isPresent(), "Результат должен быть пустым для несуществующего ID");
    }

    /**
     * Тестирует поиск пользователя по null ID.
     * Ожидается возврат пустого Optional.
     */
    @Test
    void findByIdWithNullIdShouldReturnEmpty() {
        // When
        Optional<UserEntity> result = userDao.findById(null);

        // Then
        assertFalse(result.isPresent(), "Результат должен быть пустым для null ID");
    }

    /**
     * Тестирует поиск пользователя по-отрицательному ID.
     * Ожидается возврат пустого Optional.
     */
    @Test
    void findByIdWithNegativeIdShouldReturnEmpty() {
        // When
        Optional<UserEntity> result = userDao.findById(-1L);

        // Then
        assertFalse(result.isPresent(), "Результат должен быть пустым для отрицательного ID");
    }

    /**
     * Тестирует получение всех пользователей из базы данных.
     * Ожидается возврат списка, содержащего созданных пользователей.
     */
    @Test
    void findAllWhenUsersExistShouldReturnAllUsers() {
        // Given
        UserEntity user1 = createTestUser();
        userDao.create(user1);

        UserEntity user2 = createTestUser();
        user2.setName("Another User");
        user2.setAge(30);
        userDao.create(user2);

        // When
        List<UserEntity> result = userDao.findAll();

        // Then
        assertNotNull(result, "Список пользователей не должен быть null");
        assertEquals(2, result.size(), "Должно быть 2 пользователя в списке");
    }

    /**
     * Тестирует получение всех пользователей при пустой базе данных.
     * Ожидается возврат пустого списка.
     */
    @Test
    void findAllWhenNoUsersShouldReturnEmptyList() {
        // When
        List<UserEntity> result = userDao.findAll();

        // Then
        assertNotNull(result, "Список пользователей не должен быть null");
        assertTrue(result.isEmpty(), "Список должен быть пустым при отсутствии пользователей");
    }

    /**
     * Тестирует обновление существующего пользователя.
     * Ожидается, что данные пользователя будут обновлены в базе данных.
     */
    @Test
    void updateWithExistingUserShouldUpdateUserInDatabase() {
        // Given
        UserEntity testUser = createTestUser();
        userDao.create(testUser);

        String updatedName = "Updated Name";
        String updatedEmail = "updated" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        int updatedAge = 35;

        testUser.setName(updatedName);
        testUser.setEmail(updatedEmail);
        testUser.setAge(updatedAge);

        // When
        userDao.update(testUser);

        // Then
        Optional<UserEntity> updatedUser = userDao.findById(testUser.getId());
        assertTrue(updatedUser.isPresent(), "Обновленный пользователь должен существовать");
        assertEquals(updatedName, updatedUser.get().getName(), "Имя должно быть обновлено");
        assertEquals(updatedEmail, updatedUser.get().getEmail(), "Email должен быть обновлен");
        assertEquals(updatedAge, updatedUser.get().getAge(), "Возраст должен быть обновлен");
    }

    /**
     * Тестирует обновление несуществующего пользователя.
     * Ожидается выброс исключения UserServiceException.
     */
    @Test
    void updateWithNonExistingUserShouldThrowException() {
        // Given
        UserEntity nonExistingUser = createTestUser();
        nonExistingUser.setId(999L);

        // When & Then
        Exception exception = assertThrows(UserServiceException.class,
                () -> userDao.update(nonExistingUser),
                "Должно быть выброшено исключение при попытке обновить несуществующего пользователя");
        assertTrue(exception.getMessage().contains("не найден"),
                "Сообщение должно содержать информацию о том, что пользователь не найден");
    }

    /**
     * Тестирует обновление null пользователя.
     * Ожидается выброс исключения UserServiceException.
     */
    @Test
    void updateWithNullUserShouldThrowException() {
        // When & Then
        Exception exception = assertThrows(UserServiceException.class,
                () -> userDao.update(null),
                "Должно быть выброшено исключение при попытке обновить null пользователя");
        assertTrue(exception.getMessage().contains("не могут быть null"),
                "Сообщение должно содержать информацию о null значении");
    }

    /**
     * Тестирует удаление пользователя по-существующему ID.
     * Ожидается успешное удаление и возврат true.
     */
    @Test
    void deleteWithExistingIdShouldDeleteUserAndReturnTrue() {
        // Given
        UserEntity testUser = createTestUser();
        userDao.create(testUser);
        Long userId = testUser.getId();

        // When
        boolean result = userDao.delete(userId);

        // Then
        assertTrue(result, "Удаление должно завершиться успешно");
        Optional<UserEntity> deletedUser = userDao.findById(userId);
        assertFalse(deletedUser.isPresent(), "Пользователь должен быть удален из базы данных");
    }

    /**
     * Тестирует удаление пользователя по-несуществующему ID.
     * Ожидается возврат false.
     */
    @Test
    void deleteWithNonExistingIdShouldReturnFalse() {
        // When
        boolean result = userDao.delete(999L);

        // Then
        assertFalse(result, "Удаление несуществующего пользователя должно вернуть false");
    }

    /**
     * Тестирует удаление пользователя по null ID.
     * Ожидается возврат false.
     */
    @Test
    void deleteWithNullIdShouldReturnFalse() {
        // When
        boolean result = userDao.delete(null);

        // Then
        assertFalse(result, "Удаление с null ID должно вернуть false");
    }

    /**
     * Тестирует удаление пользователя по-отрицательному ID.
     * Ожидается возврат false.
     */
    @Test
    void deleteWithNegativeIdShouldReturnFalse() {
        // When
        boolean result = userDao.delete(-1L);

        // Then
        assertFalse(result, "Удаление с отрицательным ID должно вернуть false");
    }
}
