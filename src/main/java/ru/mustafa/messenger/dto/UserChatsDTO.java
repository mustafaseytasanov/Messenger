package ru.mustafa.messenger.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Data transfer object representing a summary of a user's chat.
 *
 * @author Mustafa
 * @version 1.0
 * @param name        the display name of the chat
 * @param lastMessage the timestamp of the most recent message in the chat
 */
@Schema(description = "Ответ с чатами пользователя")
public record UserChatsDTO(
    @Schema(description = "Имя чата", example = "chat 1")
    String name,
    @Schema(description = "Время и дата последнего сообщения",
            example = "2026-05-14T15:30:00")
    LocalDateTime lastMessage
) {}