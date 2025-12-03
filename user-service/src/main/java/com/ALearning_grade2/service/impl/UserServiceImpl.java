package com.ALearning_grade2.service.impl;

import com.ALearning_grade2.dto.UserDTO;
import com.ALearning_grade2.dto.UserEventDTO;
import com.ALearning_grade2.entity.UserEntity;
import com.ALearning_grade2.exception.InvalidUserException;
import com.ALearning_grade2.repository.UserRepository;
import com.ALearning_grade2.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для управления пользователями.
 * Выполняет валидацию данных и делегирует операции доступа к данным UserRepository.
 *
 * @author ALearning_grade2
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final KafkaTemplate<String, UserEventDTO> kafkaTemplate;




    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDTO) throws InvalidUserException {
        log.debug("Создание пользователя: {}", userDTO.getEmail());
        validateUser(userDTO);

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new InvalidUserException("Пользователь с email " + userDTO.getEmail() + " уже существует");
        }

        UserEntity userEntity = convertToEntity(userDTO);
        userEntity.setCreatedAt(LocalDateTime.now());
        UserEntity savedUser = userRepository.save(userEntity);
        kafkaTemplate.send("user-events", new UserEventDTO(savedUser.getEmail(), UserEventDTO.EventType.CREATED));

        log.info("Пользователь успешно создан с ID: {}", savedUser.getId());
        return convertToDTO(savedUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserById(Long id) {
        log.debug("Поиск пользователя по ID: {}", id);
        return userRepository.findById(id)
                .map(this::convertToDTO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        log.debug("Получение списка всех пользователей");
        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) throws InvalidUserException {
        log.debug("Обновление пользователя с ID: {}", id);
        validateUser(userDTO);

        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new InvalidUserException("Пользователь с ID " + id + " не найден"));

        // Проверяем, что email не занят другим пользователем
        if (!existingUser.getEmail().equals(userDTO.getEmail()) &&
                userRepository.existsByEmail(userDTO.getEmail())) {
            throw new InvalidUserException("Пользователь с email " + userDTO.getEmail() + " уже существует");
        }

        existingUser.setName(userDTO.getName());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setAge(userDTO.getAge());

        UserEntity updatedUser = userRepository.save(existingUser);
        log.info("Пользователь с ID {} успешно обновлен", id);
        return convertToDTO(updatedUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public boolean deleteUser(Long id) {
        log.debug("Удаление пользователя с ID: {}", id);
        if (userRepository.existsById(id)) {
            userRepository.findById(id).ifPresent(userToDelete ->
                    kafkaTemplate.send("user-events",
                            new UserEventDTO(userToDelete.getEmail(),
                            UserEventDTO.EventType.DELETED)));
            userRepository.deleteById(id);
            log.info("Пользователь с ID {} успешно удален", id);
            return true;
        }
        log.warn("Пользователь с ID {} не найден для удаления", id);
        return false;
    }

    /**
     * Валидирует данные пользователя перед выполнением операций.
     *
     * @param userDTO пользователь для валидации
     * @throws InvalidUserException если данные пользователя не проходят валидацию
     */
    private void validateUser(UserDTO userDTO) throws InvalidUserException {
        log.debug("Валидация данных пользователя: {}", userDTO.getEmail());

        if (userDTO.getName() == null || userDTO.getName().trim().isEmpty()) {
            throw new InvalidUserException("Имя пользователя не может быть пустым");
        }
        if (userDTO.getEmail() == null || userDTO.getEmail().trim().isEmpty()) {
            throw new InvalidUserException("Email не может быть пустым");
        }
        if (userDTO.getEmail() == null || !userDTO.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new InvalidUserException("Некорректный формат email");
        }
        if (userDTO.getAge() == null || userDTO.getAge() < 0 || userDTO.getAge() > 150) {
            throw new InvalidUserException("Возраст должен быть в диапазоне 0–150");
        }
    }

    /**
     * Конвертирует Entity в DTO.
     *
     * @param userEntity сущность пользователя
     * @return DTO пользователя
     */
    private UserDTO convertToDTO(UserEntity userEntity) {
        UserDTO dto = new UserDTO();
        dto.setId(userEntity.getId());
        dto.setName(userEntity.getName());
        dto.setEmail(userEntity.getEmail());
        dto.setAge(userEntity.getAge());
        dto.setCreatedAt(userEntity.getCreatedAt());
        return dto;
    }

    /**
     * Конвертирует DTO в Entity.
     *
     * @param userDTO DTO пользователя
     * @return сущность пользователя
     */
    private UserEntity convertToEntity(UserDTO userDTO) {
        UserEntity entity = new UserEntity();
        entity.setId(userDTO.getId());
        entity.setName(userDTO.getName());
        entity.setEmail(userDTO.getEmail());
        entity.setAge(userDTO.getAge());
        entity.setCreatedAt(userDTO.getCreatedAt());
        return entity;
    }
}
