package ru.mustafa.messenger.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mustafa.messenger.dto.ChatDTO;
import ru.mustafa.messenger.dto.UserChatsDTO;
import ru.mustafa.messenger.service.ChatService;
import ru.mustafa.messenger.web.assembler.UserChatsModelAssembler;

import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Class ChatController.
 *
 * @author Mustafa
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/chats")
@Tag(name = "Чаты", description = "Управление чатами пользователя")
public class ChatController {

    private final ChatService chatService;
    private final UserChatsModelAssembler userChatsAssembler;

    /**
     * Constructor for ChatController.
     *
     * @param chatService the chat management service
     * @param userChatsAssembler the assembler for user chats hypermedia models
     */
    public ChatController(ChatService chatService,
                          UserChatsModelAssembler userChatsAssembler) {
        this.chatService = chatService;
        this.userChatsAssembler = userChatsAssembler;
    }

    /**
     * Creates a new chat.
     *
     * @param chatDTO the data container for the new chat
     * @return a response entity containing the created chat ID and links
     */
    @Operation(summary = "Создать новый чат")
    @PostMapping("/new-chat")
    public ResponseEntity<EntityModel<Map<String, Long>>> createChat(
            @Valid @RequestBody ChatDTO chatDTO) {
        long chatId = chatService.createChat(chatDTO);

        // In further, it will be created record with chat id
        Map<String, Long> responseBody = Map.of("chatId", chatId);
        EntityModel<Map<String, Long>> model = EntityModel.of(responseBody);

        model.add(linkTo(methodOn(ChatController.class).getUserChats())
                .withRel("user-chats"));


        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    /**
     * Retrieves all chats for the authenticated user.
     *
     * @return a response entity containing the list of user chats with HATEOAS links
     */
    @Operation(summary = "Получить список чатов текущего пользователя")
    @GetMapping("/get-chats")
    public ResponseEntity<CollectionModel<EntityModel<UserChatsDTO>>> getUserChats() {
        List<UserChatsDTO> userChats = chatService.getUserChats();

        List<EntityModel<UserChatsDTO>> assembledChats = userChats.stream()
                .map(userChatsAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<UserChatsDTO>> collectionModel =
                CollectionModel.of(assembledChats);
        collectionModel.add(linkTo(methodOn(ChatController.class)
                .getUserChats()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

}
