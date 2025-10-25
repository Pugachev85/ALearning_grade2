package com.ALearning_grade2.util;

import java.util.Scanner;

public class ConsoleUtil {
    private static final Scanner scanner = new Scanner(System.in);

    public static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

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

    public static void printMessage(String message) {
        System.out.println(message);
    }
}

