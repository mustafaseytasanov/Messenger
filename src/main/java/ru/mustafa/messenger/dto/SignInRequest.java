package ru.mustafa.messenger.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data transfer object for user authentication requests.
 *
 * @author Mustafa
 * @version 1.0
 * @param username the username of the user trying to log in
 * @param password the password of the user trying to log in
 */
@Schema(description = "Запрос на аутентификацию")
public record SignInRequest(
        @Schema(description = "Имя пользователя", example = "Jon")
        @Size(min = 5, max = 50, message = "Имя пользователя должно содержать от 5 до 50 символов")
        @NotBlank(message = "Имя пользователя не может быть пустыми")
        String username,

        @Schema(description = "Пароль", example = "my_1secret1_password")
        @NotBlank(message = "Пароль не может быть пустым")
        String password
) {}