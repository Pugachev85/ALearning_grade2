//package com.ALearning_grade2;
//
//import com.ALearning_grade2.dto.UserDTO;
//import com.ALearning_grade2.exception.InvalidUserException;
//import com.ALearning_grade2.service.UserService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
///**
// * Интеграционные тесты для UserService с Testcontainers и Liquibase
// */
//@SpringBootTest
//@Testcontainers
//@ActiveProfiles("test")
//class UserServiceIntegrationTest {
//
//    @Container
//    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15")
//            .withDatabaseName("testdb")
//            .withUsername("test")
//            .withPassword("test");
//
//    @DynamicPropertySource
//    static void configureProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
//        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
//        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
//        // Для отладки
//        registry.add("test.postgres.port", () -> postgreSQLContainer.getFirstMappedPort());
//    }
//
//    @Autowired
//    private UserService userService;
//
//    /**
//     * Тест проверки работы Liquibase миграций
//     */
//    @Test
//    void liquibaseMigrationsShouldWorkCorrectly() {
//        // После запуска Liquibase таблица должна быть создана
//        List<UserDTO> allUsers = userService.getAllUsers();
//        assertNotNull(allUsers);
//        // Тестовые данные могут быть пустыми или содержать данные из миграций
//    }
//
//    /**
//     * Полный цикл CRUD операций
//     */
//    @Test
//    void fullUserLifecycleShouldWorkCorrectly() {
//        // Create
//        UserDTO newUser = new UserDTO();
//        newUser.setName("Integration Test User");
//        newUser.setEmail("integration@test.com");
//        newUser.setAge(28);
//
//        UserDTO createdUser = userService.createUser(newUser);
//        assertNotNull(createdUser.getId());
//        assertEquals("integration@test.com", createdUser.getEmail());
//
//        // Read
//        Optional<UserDTO> foundUser = userService.getUserById(createdUser.getId());
//        assertTrue(foundUser.isPresent());
//        assertEquals("Integration Test User", foundUser.get().getName());
//
//        // Update
//        UserDTO updateData = new UserDTO();
//        updateData.setName("Updated Integration User");
//        updateData.setEmail("updated@test.com");
//        updateData.setAge(30);
//
//        UserDTO updatedUser = userService.updateUser(createdUser.getId(), updateData);
//        assertEquals("Updated Integration User", updatedUser.getName());
//
//        // Delete
//        boolean deleted = userService.deleteUser(createdUser.getId());
//        assertTrue(deleted);
//
//        // Verify deletion
//        Optional<UserDTO> deletedUser = userService.getUserById(createdUser.getId());
//        assertFalse(deletedUser.isPresent());
//    }
//
//    /**
//     * Создание пользователя с дублирующимся email
//     */
//    @Test
//    void createUserWithDuplicateEmailShouldThrowException() {
//        UserDTO user1 = new UserDTO();
//        user1.setName("User One");
//        user1.setEmail("duplicate@test.com");
//        user1.setAge(25);
//        userService.createUser(user1);
//
//        UserDTO user2 = new UserDTO();
//        user2.setName("User Two");
//        user2.setEmail("duplicate@test.com");
//        user2.setAge(30);
//
//        assertThrows(InvalidUserException.class, () -> userService.createUser(user2));
//    }
//
//    /**
//     * Получение несуществующего пользователя
//     */
//    @Test
//    void getUserByIdWithNonExistingIdShouldReturnEmpty() {
//        Optional<UserDTO> result = userService.getUserById(999L);
//        assertFalse(result.isPresent());
//    }
//}
