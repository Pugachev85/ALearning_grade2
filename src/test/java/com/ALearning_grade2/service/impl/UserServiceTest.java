package com.ALearning_grade2.service.impl;

import com.ALearning_grade2.dto.UserDTO;
import com.ALearning_grade2.entity.UserEntity;
import com.ALearning_grade2.exception.InvalidUserException;
import com.ALearning_grade2.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit-тесты для UserService
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDTO validUserDTO;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        validUserDTO = new UserDTO();
        validUserDTO.setName("Test User");
        validUserDTO.setEmail("test@example.com");
        validUserDTO.setAge(25);

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("Test User");
        userEntity.setEmail("test@example.com");
        userEntity.setAge(25);
        userEntity.setCreatedAt(LocalDateTime.now());
    }

    /**
     * Создание пользователя с валидными данными
     */
    @Test
    void createUserWithValidDataShouldCreateUser() {
        when(userRepository.existsByEmail(validUserDTO.getEmail())).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        UserDTO result = userService.createUser(validUserDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    /**
     * Создание пользователя с существующим email
     */
    @Test
    void createUserWithExistingEmailShouldThrowException() {
        when(userRepository.existsByEmail(validUserDTO.getEmail())).thenReturn(true);

        assertThrows(InvalidUserException.class, () -> userService.createUser(validUserDTO));
    }

    /**
     * Создание пользователя с невалидным email
     */
    @Test
    void createUserWithInvalidEmailShouldThrowException() {
        validUserDTO.setEmail("invalid-email");

        assertThrows(InvalidUserException.class, () -> userService.createUser(validUserDTO));
    }

    /**
     * Создание пользователя с пустым именем
     */
    @Test
    void createUserWithNullNameShouldThrowException() {
        validUserDTO.setName(null);

        assertThrows(InvalidUserException.class, () -> userService.createUser(validUserDTO));
    }

    /**
     * Поиск пользователя по существующему ID
     */
    @Test
    void getUserByIdWithExistingIdShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        Optional<UserDTO> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    /**
     * Поиск пользователя по несуществующему ID
     */
    @Test
    void getUserByIdWithNonExistingIdShouldReturnEmpty() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<UserDTO> result = userService.getUserById(999L);

        assertFalse(result.isPresent());
    }

    /**
     * Получение списка всех пользователей
     */
    @Test
    void getAllUsersShouldReturnAllUsers() {
        UserEntity user2 = new UserEntity(2L, "User2", "user2@test.com", 30, LocalDateTime.now());
        when(userRepository.findAll()).thenReturn(Arrays.asList(userEntity, user2));

        List<UserDTO> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("test@example.com", result.get(0).getEmail());
        assertEquals("user2@test.com", result.get(1).getEmail());
    }

    /**
     * Обновление пользователя с валидными данными
     */
    @Test
    void updateUserWithValidDataShouldUpdateUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        validUserDTO.setEmail("updated@example.com");
        validUserDTO.setName("Updated Name");

        UserDTO result = userService.updateUser(1L, validUserDTO);

        assertNotNull(result);
        assertEquals("updated@example.com", result.getEmail());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    /**
     * Удаление существующего пользователя
     */
    @Test
    void deleteUserWithExistingIdShouldReturnTrue() {
        when(userRepository.existsById(1L)).thenReturn(true);

        boolean result = userService.deleteUser(1L);

        assertTrue(result);
        verify(userRepository, times(1)).deleteById(1L);
    }

    /**
     * Удаление несуществующего пользователя
     */
    @Test
    void deleteUserWithNonExistingIdShouldReturnFalse() {
        when(userRepository.existsById(999L)).thenReturn(false);

        boolean result = userService.deleteUser(999L);

        assertFalse(result);
        verify(userRepository, never()).deleteById(any());
    }
}
