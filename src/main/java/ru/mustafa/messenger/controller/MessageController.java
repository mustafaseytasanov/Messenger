package ru.mustafa.messenger.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mustafa.messenger.dto.*;
import ru.mustafa.messenger.service.MessageService;
import ru.mustafa.messenger.service.RateLimiterService;
import ru.mustafa.messenger.service.UserService;
import ru.mustafa.messenger.web.assembler.ChatMessagesModelAssembler;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Class MessageController.
 *
 * @author Mustafa
 * @version 1.2
 */
@RestController
@RequestMapping("/api/v1/messages")
@Tag(name = "Сообщения",
        description = "Управление сообщениями и получение истории переписки")
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;
    private final ChatMessagesModelAssembler chatMessagesAssembler;
    private final RateLimiterService rateLimiterService;

    /**
     * Constructor for MessageController.
     *
     * @param messageService the message management service
     */
    public MessageController(MessageService messageService, UserService userService, ChatMessagesModelAssembler chatMessagesAssembler, RateLimiterService rateLimiterService) {
        this.messageService = messageService;
        this.userService = userService;
        this.chatMessagesAssembler = chatMessagesAssembler;
        this.rateLimiterService = rateLimiterService;
    }

    /**
     * Sends and creates a new message in a chat.
     *
     * @param idempotencyKey idempotency key of request
     * @param messageDTO the data container for the new message
     * @return a response entity containing the created message ID
     */
    @Operation(summary = "Отправить новое сообщение")
    @PostMapping("/new-message")
    public ResponseEntity<EntityModel<Map<String, Long>>> createMessage(
            @RequestHeader("X-Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody MessageDTO messageDTO) {

        // Limit: maximum 5 requests per second for this userId
        boolean allowed = rateLimiterService.isAllowed("create_message",
                userService.getCurrentUser().getId(),
                5,
                Duration.ofSeconds(1));

        if (!allowed) {
            Map<String, Long> errorDetails = Map.of("retry_after_seconds", 1L);
            return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(EntityModel.of(errorDetails));
        }


        long messageId = messageService.createMessage(idempotencyKey,
                messageDTO);

        Map<String, Long> responseBody = Map.of("messageId", messageId);
        EntityModel<Map<String, Long>> model = EntityModel.of(responseBody);

        model.add(linkTo(methodOn(MessageController.class)
                .getChatMessages(messageDTO.chatId(), null, 20))
                .withRel("chat-messages"));

        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @Operation(summary = "Отправить новое сообщение в Избранное")
    @PostMapping("/new-saved-message")
    public ResponseEntity<Map<String, Long>> createSavedMessage(
            @RequestHeader("X-Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody RequestSavedMessageDTO requestSavedMessageDTO) {

        Long userId = userService.getCurrentUser().getId();

        long messageId = messageService.createSavedMessage(idempotencyKey,
                requestSavedMessageDTO, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("messageId", messageId));
    }

    /**
     * Retrieves all messages for a specific chat sorted in ascending
     * chronological order. Second approach of pagination
     *
     * @param chatId the unique identifier of the chat
     * @param cursor token in base64 form
     * @param size size of page
     * @return a response entity containing the list of chat messages
     */
    @Operation(summary = "Получить сообщения чата")
    @GetMapping("/get/{chatId}")
    public ResponseEntity<CollectionModel<EntityModel
            <ChatMessagesDTO>>> getChatMessages(
                    @PathVariable Long chatId,
                    @RequestParam(required = false) String cursor,
                    @RequestParam(defaultValue = "20") int size) {
        ChatMessagesResponse response = messageService
                .getChatMessages(chatId, cursor, size);

        List<EntityModel<ChatMessagesDTO>> assembledMessages = response
                .messages().stream()
                .map(chatMessagesAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<ChatMessagesDTO>> collectionModel =
                CollectionModel.of(assembledMessages);

        // Adding self link
        collectionModel.add(linkTo(methodOn(MessageController.class)
                .getChatMessages(chatId, cursor, size))
                .withSelfRel());

        // Adding "next" link
        if (response.hasNext() && response.nextCursor() != null) {
            Link nextLink = linkTo(methodOn(MessageController.class)
                    .getChatMessages(chatId, response.nextCursor(), size))
                    .withRel("next");

            collectionModel.add(nextLink);
        }

        return ResponseEntity.ok(collectionModel);

    }

    @Operation(summary = "Получить сохраненные сообщения с пагинацией")
    @GetMapping("/get/saved")
    public ResponseEntity<PagedModel<EntityModel<SavedMessageDTO>>> getSavedMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            PagedResourcesAssembler<SavedMessageDTO> pagedAssembler) {

        Long userId = userService.getCurrentUser().getId();

        Page<SavedMessageDTO> messagesPage = messageService
                .getSavedMessagesHistory(userId, page, size);

        PagedModel<EntityModel<SavedMessageDTO>> pagedModel
                = pagedAssembler.toModel(messagesPage);

        return ResponseEntity.ok(pagedModel);
    }
}
