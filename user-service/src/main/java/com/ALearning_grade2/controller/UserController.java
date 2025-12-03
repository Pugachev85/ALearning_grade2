package com.ALearning_grade2.controller;

import com.ALearning_grade2.dto.UserDTO;
import com.ALearning_grade2.exception.InvalidUserException;
import com.ALearning_grade2.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Контроллер для управления пользователями через веб-интерфейс.
 * Предоставляет методы для выполнения CRUD операций над пользователями
 * и отображения соответствующих представлений.
 */
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Отображает страницу со списком всех пользователей.
     *
     * @param model объект Model для передачи данных в представление
     * @return имя представления для отображения списка пользователей
     */
    @GetMapping
    public String listUsers(Model model) {
        log.info("Запрос на получение списка пользователей");
        model.addAttribute("users", userService.getAllUsers());
        return "users/list";
    }

    /**
     * Отображает форму для создания нового пользователя.
     *
     * @param model объект Model для передачи данных в представление
     * @return имя представления формы создания пользователя
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        log.info("Запрос на отображение формы создания пользователя");
        model.addAttribute("userDTO", new UserDTO());
        return "users/create";
    }

    /**
     * Обрабатывает создание нового пользователя.
     *
     * @param userDTO            объект UserDTO с данными пользователя
     * @param bindingResult      объект для хранения результатов валидации
     * @param redirectAttributes атрибуты для перенаправления
     * @param model              объект Model для передачи данных в представление
     * @return перенаправление на список пользователей при успехе или возврат к форме при ошибке
     */
    @PostMapping
    public String createUser(@Valid @ModelAttribute("userDTO") UserDTO userDTO,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        log.info("Запрос на создание пользователя: {}", userDTO.getEmail());

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при создании пользователя: {}", bindingResult.getAllErrors());
            return "users/create";
        }

        try {
            userService.createUser(userDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Пользователь успешно создан!");
            log.info("Пользователь успешно создан: {}", userDTO.getEmail());
            return "redirect:/users";
        } catch (InvalidUserException e) {
            log.error("Ошибка при создании пользователя: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "users/create";
        }
    }

    /**
     * Отображает детальную информацию о пользователе.
     *
     * @param id                 идентификатор пользователя
     * @param model              объект Model для передачи данных в представление
     * @param redirectAttributes атрибуты для перенаправления
     * @return имя представления с детальной информацией о пользователе или перенаправление при ошибке
     */
    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        log.info("Запрос на просмотр пользователя с ID: {}", id);

        return userService.getUserById(id)
                .map(userDTO -> {
                    model.addAttribute("user", userDTO);
                    return "users/view";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Пользователь с ID " + id + " не найден");
                    return "redirect:/users";
                });
    }

    /**
     * Отображает форму для редактирования пользователя.
     *
     * @param id                 идентификатор пользователя
     * @param model              объект Model для передачи данных в представление
     * @param redirectAttributes атрибуты для перенаправления
     * @return имя представления формы редактирования или перенаправление при ошибке
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        log.info("Запрос на отображение формы редактирования пользователя с ID: {}", id);

        return userService.getUserById(id)
                .map(userDTO -> {
                    model.addAttribute("userDTO", userDTO);
                    return "users/edit";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Пользователь с ID " + id + " не найден");
                    return "redirect:/users";
                });
    }

    /**
     * Обрабатывает обновление данных пользователя.
     *
     * @param id                 идентификатор пользователя
     * @param userDTO            объект UserDTO с обновленными данными
     * @param bindingResult      объект для хранения результатов валидации
     * @param redirectAttributes атрибуты для перенаправления
     * @param model              объект Model для передачи данных в представление
     * @return перенаправление на список пользователей при успехе или возврат к форме при ошибке
     */
    @PutMapping("/{id}")
    public String updateUser(@PathVariable Long id,
                             @Valid @ModelAttribute("userDTO") UserDTO userDTO,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        log.info("Запрос на обновление пользователя с ID: {}", id);

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при обновлении пользователя: {}", bindingResult.getAllErrors());
            return "users/edit";
        }

        try {
            userService.updateUser(id, userDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Пользователь успешно обновлен!");
            log.info("Пользователь с ID {} успешно обновлен", id);
            return "redirect:/users";
        } catch (InvalidUserException e) {
            log.error("Ошибка при обновлении пользователя: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "users/edit";
        }
    }

    /**
     * Обрабатывает удаление пользователя.
     *
     * @param id                 идентификатор пользователя
     * @param redirectAttributes атрибуты для перенаправления
     * @return перенаправление на список пользователей
     */
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("Запрос на удаление пользователя с ID: {}", id);

        if (userService.deleteUser(id)) {
            redirectAttributes.addFlashAttribute("successMessage", "Пользователь успешно удален!");
            log.info("Пользователь с ID {} успешно удален", id);
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Пользователь с ID " + id + " не найден");
            log.warn("Пользователь с ID {} не найден для удаления", id);
        }

        return "redirect:/users";
    }
}

