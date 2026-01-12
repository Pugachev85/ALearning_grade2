package com.ALearning_grade2.gateway.fallback;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Контроллер‑обработчик fallback‑ответов, используемых в фильтре
 * {@code Retry} шлюза (Spring Cloud Gateway).
 * <p>
 * Когда один из downstream‑сервисов (user‑service или notification‑service)
 * недоступен, шлюз перенаправляет запрос к соответствующему эндпоинту
 * {@code /fallback/*}, где возвращается сообщение о том,
 * что сервис временно недоступен.
 * </p>
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    /**
     * Возвращает fallback‑ответ для {@code user-service}.
     *
     * @return HTTP‑ответ 503 (SERVICE_UNAVAILABLE) с русскоязычным сообщением.
     */
    @GetMapping("/user-service")
    public Mono<ResponseEntity<String>> userServiceFallback() {
        return Mono.just(
                ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Сервис пользователей временно недоступен. Пожалуйста, попробуйте позже.")
        );
    }

    /**
     * Возвращает fallback‑ответ для {@code notification-service}.
     *
     * @return HTTP‑ответ 503 (SERVICE_UNAVAILABLE) с русскоязычным сообщением.
     */
    @GetMapping("/notification-service")
    public Mono<ResponseEntity<String>> notificationServiceFallback() {
        return Mono.just(
                ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Сервис уведомлений временно недоступен. Пожалуйста, попробуйте позже.")
        );
    }
}
