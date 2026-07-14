package ru.mustafa.messenger.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mustafa.messenger.service.UserService;

/**
 * Controller demonstrating role-based access control and endpoint security.
 *
 * @author Mustafa
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/example")
@RequiredArgsConstructor
@Tag(name = "Аутентификация")
public class ExampleController {
    private final UserService service;

    /**
     * Endpoint accessible by any authenticated user.
     *
     * @return a welcome string message
     */
    @GetMapping
    @Operation(summary = "Доступен только авторизованным пользователям")
    public String example() {
        return "Hello, world!";
    }

    /**
     * Endpoint restricted to users with the ADMIN role.
     *
     * @return an admin-specific welcome string message
     */
    @GetMapping("/admin")
    @Operation(summary = "Доступен только авторизованным пользователям с ролью ADMIN")
    @PreAuthorize("hasRole('ADMIN')")
    public String exampleAdmin() {
        return "Hello, admin!";
    }

    /**
     * Grants ADMIN role to the current user for demonstration purposes.
     */
    @GetMapping("/get-admin")
    @Operation(summary = "Получить роль ADMIN (для демонстрации)")
    public void getAdmin() {
        service.getAdmin();
    }
}