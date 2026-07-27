package ru.mustafa.messenger.dto;

import java.util.List;

public record ChatMessagesResponse(
        List<ChatMessagesDTO> messages,
        String nextCursor,
        boolean hasNext
) {}
