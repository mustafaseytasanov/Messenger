package ru.mustafa.messenger.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Event published when a new user is successfully registered in the system.
 *
 * @param userId   the unique identifier of the registered user
 * @param username the unique username (login) of the user in the system
 *
 * @author Mustafa
 * @version 1.0
 */
public record UserRegisteredEvent(
        @Schema(description = "id пользователя", example = "1")
        Long userId,
        @Schema(description = "Имя пользователя", example = "must99")
        String username) {
}

