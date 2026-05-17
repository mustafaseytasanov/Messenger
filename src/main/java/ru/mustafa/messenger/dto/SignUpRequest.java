package ru.mustafa.messenger.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data transfer object for user registration requests.
 *
 * @author Mustafa
 * @version 1.0
 * @param username the desired username for the new account
 * @param email    the user's email address
 * @param password the chosen password for the new account
 */
@Schema(description = "Запрос на регистрацию")
public record SignUpRequest(
        @Schema(description = "Имя пользователя", example = "Jon")
        @Size(min = 5, max = 50, message = "Имя пользователя должно содержать от 5 до 50 символов")
        @NotBlank(message = "Имя пользователя не может быть пустыми")
        String username,

        @Schema(description = "Адрес электронной почты", example = "jondoe@gmail.com")
        @Size(min = 5, max = 255, message = "Адрес электронной почты должен содержать от 5 до 255 символов")
        @NotBlank(message = "Адрес электронной почты не может быть пустыми")
        @Email(message = "Email адрес должен быть в формате user@example.com")
        String email,

        @Schema(description = "Пароль", example = "my_1secret1_password")
        @Size(min = 8, max = 255, message = "Длина пароля должна быть 8 - 255 символов")
        String password
) {}
