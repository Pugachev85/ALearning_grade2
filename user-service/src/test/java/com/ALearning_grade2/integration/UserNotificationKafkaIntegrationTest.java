package com.ALearning_grade2.integration;

import com.ALearning_grade2.dto.UserDTO;
import com.ALearning_grade2.dto.UserEventDTO;
import com.ALearning_grade2.repository.UserRepository;
import com.ALearning_grade2.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Полный интеграционный тест с эмуляцией Kafka для проверки обмена сообщениями
 * между user-service и notification-service
 */
@SpringBootTest
@EmbeddedKafka(
        partitions = 1,
        topics = {"user-events"},
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:9092",
                "port=9092"
        }
)
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.group-id=test-group",
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
        "spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer",
        "spring.kafka.consumer.properties.spring.json.trusted.packages=com.ALearning_grade2.dto",
        "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
        "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer"
})
@DirtiesContext
class UserNotificationKafkaIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestKafkaConsumer testConsumer;

    @BeforeEach
    void setUp() {
        // Очищаем базу данных перед каждым тестом
        userRepository.deleteAll();

        // Очищаем состояние consumer
        testConsumer.reset();
    }

    /**
     * Генерация уникального email для тестов
     */
    private String generateUniqueEmail(String baseName) {
        return baseName + "-" + UUID.randomUUID().toString().substring(0, 8) + "@test.com";
    }

    /**
     * Проверка отправки сообщения в Kafka при создании пользователя
     * и корректного получения этого сообщения
     */
    @Test
    void createUserShouldSendMessageToKafkaAndBeReceived() throws InterruptedException {
        // Given
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Kafka Integration Test User");
        String uniqueEmail = generateUniqueEmail("kafka-integration");
        userDTO.setEmail(uniqueEmail);
        userDTO.setAge(25);

        // When
        UserDTO createdUser = userService.createUser(userDTO);

        // Then
        boolean messageReceived = testConsumer.awaitMessage(15, TimeUnit.SECONDS);

        assertThat(messageReceived).as("Message should be received within timeout").isTrue();
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getEmail()).isEqualTo(uniqueEmail);

        // Проверяем содержание полученного сообщения
        UserEventDTO receivedEvent = testConsumer.getLastReceivedEvent();
        assertThat(receivedEvent).isNotNull();
        assertThat(receivedEvent.getEmail()).isEqualTo(uniqueEmail);
        assertThat(receivedEvent.getEventType()).isEqualTo(UserEventDTO.EventType.CREATED);
    }

    /**
     * Проверка отправки сообщения в Kafka при удалении пользователя
     * и корректного получения этого сообщения
     */
    @Test
    void deleteUserShouldSendMessageToKafkaAndBeReceived() throws InterruptedException {
        // Given
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Delete Kafka Test User");
        String uniqueEmail = generateUniqueEmail("delete-kafka");
        userDTO.setEmail(uniqueEmail);
        userDTO.setAge(30);

        UserDTO createdUser = userService.createUser(userDTO);

        testConsumer.reset();

        // When - удаляем пользователя
        boolean deleted = userService.deleteUser(createdUser.getId());

        // Then
        assertThat(deleted).isTrue();

        boolean messageReceived = testConsumer.awaitMessage(15, TimeUnit.SECONDS);

        assertThat(messageReceived).as("Delete message should be received within timeout").isTrue();

        UserEventDTO receivedEvent = testConsumer.getLastReceivedEvent();
        assertThat(receivedEvent).isNotNull();
        assertThat(receivedEvent.getEmail()).isEqualTo(uniqueEmail);
        assertThat(receivedEvent.getEventType()).isEqualTo(UserEventDTO.EventType.DELETED);
    }
}
