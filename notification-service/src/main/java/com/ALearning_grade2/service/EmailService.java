package com.ALearning_grade2.service;

/**
 * Сервис для отправки email-сообщений
 */
public interface EmailService {
    /**
     * Отправляет email
     *
     * @param to получатель
     * @param subject тема письма
     * @param text текст письма
     */
    void sendEmail(String to, String subject, String text);
}