package ru.mustafa.messenger.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * Data transfer object for sending a new message.
 *
 * @author Mustafa
 * @version 1.0
 * @param chatId the unique identifier of the target chat
 * @param text   the textual content of the message
 */
@Builder
@Schema(description = "Данные для отправки нового сообщения")
public record MessageDTO(
        @NotNull(message = "chat id не может быть null")
        @Schema(description = "ID чата", example = "4")
        long chatId,

        @Schema(description = "Текст сообщения", example = "Привет!")
        @Size(min = 1, max = 200, message = "Размер: 1 - 200 символов")
        @NotBlank(message = "Сообщение не может быть пустым")
        String text
) {}
