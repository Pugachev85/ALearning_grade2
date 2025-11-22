package com.ALearning_grade2.controller;

import com.ALearning_grade2.dto.UserDTO;
import com.ALearning_grade2.exception.InvalidUserException;
import com.ALearning_grade2.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * REST контроллер для управления пользователями с поддержкой HATEOAS и OpenAPI документации.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Пользователи", description = "API для управления пользователями")
public class UserRestController {

    private final UserService userService;

    /**
     * Создает нового пользователя
     */
    @Operation(summary = "Создать пользователя", description = "Создает нового пользователя с указанными данными")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно создан",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные пользователя"),
            @ApiResponse(responseCode = "409", description = "Пользователь с таким email уже существует")
    })
    @PostMapping
    public ResponseEntity<EntityModel<UserDTO>> createUser(
            @Parameter(description = "Данные нового пользователя")
            @Valid @RequestBody UserDTO userDTO) {
        log.info("Запрос на создание пользователя: {}", userDTO.getEmail());

        try {
            UserDTO createdUser = userService.createUser(userDTO);
            EntityModel<UserDTO> userResource = EntityModel.of(createdUser);
            addLinks(userResource);

            return ResponseEntity.status(HttpStatus.CREATED).body(userResource);
        } catch (InvalidUserException e) {
            log.error("Ошибка при создании пользователя: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * Получает пользователя по ID
     */
    @Operation(summary = "Получить пользователя", description = "Получает информацию о пользователе по его ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь найден",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserDTO>> getUserById(
            @Parameter(description = "ID пользователя")
            @PathVariable Long id) {
        log.info("Запрос на получение пользователя с ID: {}", id);

        return userService.getUserById(id)
                .map(userDTO -> {
                    EntityModel<UserDTO> userResource = EntityModel.of(userDTO);
                    addLinks(userResource);
                    return ResponseEntity.ok(userResource);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Получает список всех пользователей
     */
    @Operation(summary = "Получить всех пользователей", description = "Получает список всех зарегистрированных пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список пользователей успешно получен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class)))
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UserDTO>>> getAllUsers() {
        log.info("Запрос на получение списка всех пользователей");

        List<EntityModel<UserDTO>> userResources = userService.getAllUsers().stream()
                .map(user -> {
                    EntityModel<UserDTO> resource = EntityModel.of(user);
                    addLinks(resource);
                    return resource;
                })
                .collect(Collectors.toList());

        CollectionModel<EntityModel<UserDTO>> collectionModel = CollectionModel.of(userResources);
        collectionModel.add(linkTo(methodOn(UserRestController.class).getAllUsers()).withSelfRel());
        collectionModel.add(linkTo(methodOn(UserRestController.class).createUser(null)).withRel("create-user"));

        return ResponseEntity.ok(collectionModel);
    }

    /**
     * Обновляет пользователя
     */
    @Operation(summary = "Обновить пользователя", description = "Обновляет информацию о существующем пользователе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные пользователя"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "409", description = "Пользователь с таким email уже существует")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UserDTO>> updateUser(
            @Parameter(description = "ID пользователя") @PathVariable Long id,
            @Parameter(description = "Обновленные данные пользователя")
            @Valid @RequestBody UserDTO userDTO) {
        log.info("Запрос на обновление пользователя с ID: {}", id);

        try {
            UserDTO updatedUser = userService.updateUser(id, userDTO);
            EntityModel<UserDTO> userResource = EntityModel.of(updatedUser);
            addLinks(userResource);

            return ResponseEntity.ok(userResource);
        } catch (InvalidUserException e) {
            log.error("Ошибка при обновлении пользователя: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * Удаляет пользователя
     */
    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя по его ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Пользователь успешно удален"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID пользователя")
            @PathVariable Long id) {
        log.info("Запрос на удаление пользователя с ID: {}", id);

        if (userService.deleteUser(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Добавляет HATEOAS ссылки к ресурсу пользователя
     */
    private void addLinks(EntityModel<UserDTO> userResource) {
        UserDTO user = userResource.getContent();
        if (user != null && user.getId() != null) {
            // Self ссылка
            userResource.add(linkTo(methodOn(UserRestController.class).getUserById(user.getId())).withSelfRel());

            // Ссылка на все пользователей
            userResource.add(linkTo(methodOn(UserRestController.class).getAllUsers()).withRel("all-users"));

            // Ссылка на обновление
            userResource.add(linkTo(methodOn(UserRestController.class).updateUser(user.getId(), null))
                    .withRel("update")
                    .withType("PUT"));

            // Ссылка на удаление
            userResource.add(linkTo(methodOn(UserRestController.class).deleteUser(user.getId()))
                    .withRel("delete")
                    .withType("DELETE"));

            // Можно также добавить профильные ссылки
            userResource.add(linkTo(methodOn(UserRestController.class).getUserById(user.getId()))
                    .withRel("profile"));
        }
    }

}
