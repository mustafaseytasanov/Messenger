package ru.mustafa.messenger.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mustafa.messenger.dto.ChatDTO;
import ru.mustafa.messenger.dto.UserChatsDTO;
import ru.mustafa.messenger.service.ChatService;

import java.util.List;

/**
 * Class ChatController.
 *
 * @author Mustafa
 * @version 1.0
 */
@RestController
@RequestMapping("/chats")
@Tag(name = "Чаты", description = "Управление чатами пользователя")
public class ChatController {

    private final ChatService chatService;

    /**
     * Constructor for ChatController.
     *
     * @param chatService the chat management service
     */
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Creates a new chat.
     *
     * @param chatDTO the data container for the new chat
     * @return a response entity containing the created chat ID
     */
    @Operation(summary = "Создать новый чат")
    @PostMapping("/new-chat")
    public ResponseEntity<?> createChat(@Valid @RequestBody ChatDTO chatDTO) {
        long chatId = chatService.createChat(chatDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("new chat id: " + chatId);
    }

    /**
     * Retrieves all chats for the authenticated user.
     *
     * @return a response entity containing the list of user chats
     */
    @Operation(summary = "Получить список чатов текущего пользователя")
    @GetMapping("/get-chats")
    public ResponseEntity<?> getUserChats() {
        List<UserChatsDTO> userChats = chatService.getUserChats();
        return ResponseEntity.status(HttpStatus.OK)
                .body("My recent chats:  " + userChats);
    }

}
