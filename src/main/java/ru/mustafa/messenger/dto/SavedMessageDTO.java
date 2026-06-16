package ru.mustafa.messenger.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Data transfer object representing a message in the saved chat.
 *
 * @author Mustafa
 * @version 1.0
 * @param text      the textual content of the message
 * @param createdAt the timestamp when the message was created
 */
@Schema(description = "Сообщение из истории чата")
public record SavedMessageDTO(
        @Schema(description = "Текст сообщения",
                example = "Привет! Как дела?")
        String text,
        @Schema(description = "Время отправки сообщения",
                example = "2026-05-17T15:35:00")
        LocalDateTime createdAt
) {
}