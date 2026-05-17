package ru.mustafa.messenger.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * Data transfer object for adding a new chat session.
 *
 * @author Mustafa
 * @version 1.0
 * @param name  the unique name of the chat
 * @param users the set of user IDs to be included in the chat
 */
@Builder
@Schema(description = "Добавление чата")
public record ChatDTO(

    @Schema(description = "Название чата", example = "Чат семьи")
    @Size(min = 1, max = 100, message = "Размер: 1 - 100 символов")
    @NotBlank(message = "Имя чата не должно быть пустым")
    String name,

    @NotNull
    @Schema(description = "Список ID пользователей чата", example = "[1, 2]")
    @Size(min = 1, message = "Вы должны добавить хотя бы одного человека")
    @NotEmpty(message = "Список не должен быть пустым")
    Set<Long> users
){}
