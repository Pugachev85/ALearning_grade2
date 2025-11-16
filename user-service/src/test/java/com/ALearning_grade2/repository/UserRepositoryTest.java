package com.ALearning_grade2.repository;

import com.ALearning_grade2.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Data JPA тесты для UserRepository с Testcontainers
 */
@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    private UserEntity createTestUser() {
        UserEntity user = new UserEntity();
        user.setName("Repository Test User");
        user.setEmail("repo@test.com");
        user.setAge(25);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    /**
     * Сохранение пользователя в БД
     */
    @Test
    void saveUserShouldPersistUser() {
        UserEntity user = createTestUser();
        UserEntity savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertEquals("repo@test.com", savedUser.getEmail());
    }

    /**
     * Проверка существования email
     */
    @Test
    void existsByEmailWithExistingEmailShouldReturnTrue() {
        UserEntity user = createTestUser();
        userRepository.save(user);

        boolean exists = userRepository.existsByEmail("repo@test.com");

        assertTrue(exists);
    }

    /**
     * Проверка несуществующего email
     */
    @Test
    void existsByEmailWithNonExistingEmailShouldReturnFalse() {
        boolean exists = userRepository.existsByEmail("nonexistent@test.com");

        assertFalse(exists);
    }
}
