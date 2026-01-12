package com.ALearning_grade2.controller;

import com.ALearning_grade2.dto.UserDTO;
import com.ALearning_grade2.exception.InvalidUserException;
import com.ALearning_grade2.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserApiController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAll() {
        log.info("GET /api/users – список всех");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
        log.info("GET /api/users/{} – деталь", id);
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserDTO> create(@Valid @RequestBody UserDTO dto) {
        log.info("POST /api/users – создание {}", dto.getEmail());
        try {
            UserDTO created = userService.createUser(dto);
            return ResponseEntity.status(201).body(created);
        } catch (InvalidUserException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id,
                                          @Valid @RequestBody UserDTO dto) {
        log.info("PUT /api/users/{} – обновление", id);
        try {
            return ResponseEntity.ok(userService.updateUser(id, dto));
        } catch (InvalidUserException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/users/{} – удаление", id);
        boolean removed = userService.deleteUser(id);
        return removed ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
