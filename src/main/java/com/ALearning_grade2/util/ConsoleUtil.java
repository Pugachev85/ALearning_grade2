package com.ALearning_grade2.util;

import java.util.Scanner;

/**
 * Утилитарный класс для работы с консольным вводом/выводом.
 * Предоставляет методы для чтения данных разных типов из консоли.
 */
public class ConsoleUtil {
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Читает строку из консоли с выводом приглашения.
     *
     * @param prompt приглашение для ввода
     * @return введенная пользователем строка
     */
    public static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Читает целое число из консоли с выводом приглашения.
     *
     * @param prompt приглашение для ввода
     * @return введенное пользователем целое число
     */
    public static int readInt(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.println("Пожалуйста, введите целое число.");
            scanner.next();
        }
        int value = scanner.nextInt();
        scanner.nextLine();
        return value;
    }

    /**
     * Читает длинное целое число из консоли с выводом приглашения.
     *
     * @param prompt приглашение для ввода
     * @return введенное пользователем длинное целое число
     */
    public static Long readLong(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextLong()) {
            System.out.println("Пожалуйста, введите корректное число.");
            scanner.next();
        }
        long value = scanner.nextLong();
        scanner.nextLine();
        return value;
    }

    /**
     * Выводит сообщение в консоль.
     *
     * @param message сообщение для вывода
     */
    public static void printMessage(String message) {
        System.out.println(message);
    }
}
