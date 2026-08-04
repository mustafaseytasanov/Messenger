package ru.mustafa.messenger.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * Data transfer object for sending a new saved message.
 *
 * @author Mustafa
 * @version 1.0
 * @param text   the textual content of the message
 */
@Builder
@Schema(description = "Данные для отправки нового сообщения в Избранное")
public record RequestSavedMessageDTO(
    @Schema(description = "Текст сообщения", example = "Привет!")
    @Size(min = 1, max = 200, message = "Размер: 1 - 200 символов")
    @NotBlank(message = "Сообщение не может быть пустым")
    String text
) {}
