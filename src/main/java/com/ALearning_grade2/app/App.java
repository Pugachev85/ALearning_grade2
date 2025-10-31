package com.ALearning_grade2.app;

import com.ALearning_grade2.dao.UserDaoImpl;
import com.ALearning_grade2.entity.UserEntity;
import com.ALearning_grade2.exception.InvalidUserException;
import com.ALearning_grade2.service.UserService;
import com.ALearning_grade2.service.impl.UserServiceImpl;
import com.ALearning_grade2.util.ConsoleUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.ALearning_grade2.util.ConsoleUtil.printMessage;

/**
 * Главный класс приложения для управления пользователями.
 * Предоставляет консольный интерфейс для выполнения CRUD операций.
 */
public class App {
    private static final Logger logger = LogManager.getLogger(App.class);
    private final UserService userService;

    /**
     * Конструктор инициализирует сервис пользователей.
     */
    public App() {
        this.userService = new UserServiceImpl(new UserDaoImpl());
    }

    public static void main(String[] args) {
        logger.info("Запуск user-service...");
        App app = new App();
        app.showMenu();
    }

    /**
     * Отображает главное меню приложения и обрабатывает пользовательский ввод.
     */
    private void showMenu() {
        while (true) {
            printMessage("\n=== User-service ===");
            printMessage("1. Создать пользователя");
            printMessage("2. Получить пользователя по ID");
            printMessage("3. Получить всех пользователей");
            printMessage("4. Обновить пользователя");
            printMessage("5. Удалить пользователя");
            printMessage("6. Выход");
            printMessage("Выберите действие (1-6): ");

            int choice = ConsoleUtil.readInt("");

            switch (choice) {
                case 1 -> createUser();
                case 2 -> getUserById();
                case 3 -> getAllUsers();
                case 4 -> updateUser();
                case 5 -> deleteUser();
                case 6 -> {
                    printMessage("До свидания!");
                    return;
                }
                default -> printMessage("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    /**
     * Создает нового пользователя на основе введенных данных.
     */
    private void createUser() {
        try {
            String name = ConsoleUtil.readString("Имя: ");
            String email = ConsoleUtil.readString("Email: ");
            int age = ConsoleUtil.readInt("Возраст: ");

            UserEntity user = new UserEntity();
            user.setName(name);
            user.setEmail(email);
            user.setAge(age);
            user.setCreatedAt(LocalDateTime.now());

            userService.createUser(user);
            printMessage("Пользователь создан!");
        } catch (InvalidUserException e) {
            printMessage("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            printMessage("Неожиданная ошибка: " + e.getMessage());
        }
    }

    /**
     * Получает и отображает пользователя по идентификатору.
     */
    private void getUserById() {
        Long id = ConsoleUtil.readLong("ID пользователя: ");
        Optional<UserEntity> user = userService.getUserById(id);
        user.ifPresentOrElse(u -> printMessage(u.toString()), () -> printMessage("Пользователь не найден"));
    }

    /**
     * Отображает список всех пользователей.
     */
    private void getAllUsers() {
        List<UserEntity> users = userService.getAllUsers();
        if (users.isEmpty()) {
            printMessage("Пользователи не найдены");
        } else {
            users.forEach(u -> printMessage(u.toString()));
        }
    }

    /**
     * Обновляет данные существующего пользователя.
     */
    private void updateUser() {
        Long id = ConsoleUtil.readLong("ID пользователя для обновления: ");
        Optional<UserEntity> existingUser = userService.getUserById(id);

        if (existingUser.isEmpty()) {
            printMessage("Пользователь не найден");
            return;
        }

        try {
            UserEntity user = existingUser.get();
            user.setName(ConsoleUtil.readString("Новое имя: "));
            user.setEmail(ConsoleUtil.readString("Новый email: "));
            user.setAge(ConsoleUtil.readInt("Новый возраст: "));

            userService.updateUser(user);
            printMessage("Пользователь обновлён!");
        } catch (InvalidUserException e) {
            printMessage("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            printMessage("Неожиданная ошибка: " + e.getMessage());
        }
    }

    /**
     * Удаляет пользователя по идентификатору.
     */
    private void deleteUser() {
        Long id = ConsoleUtil.readLong("ID пользователя для удаления: ");
        if (userService.deleteUser(id)) {
            printMessage("Пользователь удалён!");
        } else {
            printMessage("Пользователь с ID: " + id + " в базе не найден!");
        }
    }
}
