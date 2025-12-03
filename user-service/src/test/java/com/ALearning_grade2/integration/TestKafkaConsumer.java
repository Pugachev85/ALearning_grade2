package com.ALearning_grade2.integration;

import com.ALearning_grade2.dto.UserEventDTO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Тестовый Kafka consumer для получения и проверки сообщений в интеграционных тестах
 */
@Component
@Slf4j
public class TestKafkaConsumer {

    /**
     * -- GETTER --
     *  Получение последнего полученного события
     */
    @Getter
    private volatile UserEventDTO lastReceivedEvent;
    private volatile CountDownLatch latch = new CountDownLatch(1);

    /**
     * Слушатель Kafka для получения сообщений
     */
    @KafkaListener(
            topics = "user-events",
            groupId = "test-consumer-group",
            properties = {
                    "spring.json.trusted.packages=com.ALearning_grade2.dto"
            }
    )
    public void consumeUserEvent(@Payload UserEventDTO userEvent) {
        log.info("Received user event: {} for email: {}", userEvent.getEventType(), userEvent.getEmail());
        this.lastReceivedEvent = userEvent;
        latch.countDown(); // Сигнализируем о получении сообщения
    }

    /**
     * Ожидание получения сообщения
     */
    public boolean awaitMessage(long timeout, TimeUnit unit) throws InterruptedException {
        return latch.await(timeout, unit);
    }

    /**
     * Сброс состояния consumer для следующего теста
     */
    public void reset() {
        this.lastReceivedEvent = null;
        this.latch = new CountDownLatch(1);
    }
}
