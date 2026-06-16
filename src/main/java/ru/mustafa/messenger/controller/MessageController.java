package ru.mustafa.messenger.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.mustafa.messenger.dto.ChatMessagesDTO;
import ru.mustafa.messenger.dto.MessageDTO;
import ru.mustafa.messenger.dto.SavedMessageDTO;
import ru.mustafa.messenger.service.MessageService;

import java.util.List;

/**
 * Class MessageController.
 *
 * @author Mustafa
 * @version 1.1
 */
@RestController
@RequestMapping("/messages")
@Tag(name = "Сообщения",
        description = "Управление сообщениями и получение истории переписки")
public class MessageController {

    private final MessageService messageService;

    /**
     * Constructor for MessageController.
     *
     * @param messageService the message management service
     */
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Sends and creates a new message in a chat.
     *
     * @param messageDTO the data container for the new message
     * @return a response entity containing the created message ID
     */
    @Operation(summary = "Отправить новое сообщение")
    @PostMapping("/new-message")
    public ResponseEntity<?> createMessage(
            @Valid @RequestBody MessageDTO messageDTO) {

        long messageId = messageService.createMessage(messageDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("new message id: " + messageId);
    }

    /**
     * Retrieves all messages for a specific chat sorted in ascending
     * chronological order.
     *
     * @param chatId the unique identifier of the chat
     * @return a response entity containing the list of chat messages
     */
    @Operation(summary = "Получить сообщения чата")
    @GetMapping("/get/{chatId}")
    public ResponseEntity<?> getChatMessages(@PathVariable Long chatId) {
        List<ChatMessagesDTO> sortedChatMessages = messageService
                .getChatMessages(chatId);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Chat messages ascending: " + sortedChatMessages);
    }


    @GetMapping("/get/saved")
    public ResponseEntity<Page<SavedMessageDTO>> getSavedMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<SavedMessageDTO> messagesPage = messageService
                .getSavedMessagesHistory(page, size);
        return ResponseEntity.ok(messagesPage);
    }
}
