package ru.mustafa.messenger.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mustafa.messenger.service.UserService;

/**
 * Class UserController.
 *
 * @author Mustafa
 * @version 1.0
 */
@RestController
@RequestMapping("/users")
@Tag(name = "Пользователи", description = "Управление профилями и данными пользователей")
public class UserController {

    private final UserService userService;

    /**
     * Constructor for UserController.
     *
     * @param userService the user management service
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }
}
