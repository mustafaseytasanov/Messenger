package ru.mustafa.messenger.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.mustafa.messenger.dto.ChatMessagesDTO;
import ru.mustafa.messenger.dto.MessageDTO;
import ru.mustafa.messenger.dto.SavedMessageDTO;
import ru.mustafa.messenger.service.MessageService;
import ru.mustafa.messenger.web.assembler.ChatMessagesModelAssembler;

import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Class MessageController.
 *
 * @author Mustafa
 * @version 1.1
 */
@RestController
@RequestMapping("/api/v1/messages")
@Tag(name = "Сообщения",
        description = "Управление сообщениями и получение истории переписки")
public class MessageController {

    private final MessageService messageService;
    private final ChatMessagesModelAssembler chatMessagesAssembler;

    /**
     * Constructor for MessageController.
     *
     * @param messageService the message management service
     */
    public MessageController(MessageService messageService, ChatMessagesModelAssembler chatMessagesAssembler) {
        this.messageService = messageService;
        this.chatMessagesAssembler = chatMessagesAssembler;
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

        long messageId = messageService.createMessage(idempotencyKey,
                messageDTO);

        Map<String, Long> responseBody = Map.of("messageId", messageId);
        EntityModel<Map<String, Long>> model = EntityModel.of(responseBody);

        model.add(linkTo(methodOn(MessageController.class).getChatMessages(messageDTO.chatId())).withRel("chat-messages"));

        return ResponseEntity.status(HttpStatus.CREATED).body(model);
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
    public ResponseEntity<CollectionModel<EntityModel<ChatMessagesDTO>>> getChatMessages(@PathVariable Long chatId) {
        List<ChatMessagesDTO> sortedChatMessages = messageService
                .getChatMessages(chatId);

        List<EntityModel<ChatMessagesDTO>> assembledMessages = sortedChatMessages.stream()
                .map(chatMessagesAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<ChatMessagesDTO>> collectionModel =
                CollectionModel.of(assembledMessages);
        // Adding self link
        collectionModel.add(linkTo(methodOn(MessageController.class)
                .getChatMessages(chatId)).withSelfRel());
        return ResponseEntity.ok(collectionModel);
    }

    @Operation(summary = "Получить сохраненные сообщения с пагинацией")
    @GetMapping("/get/saved")
    public ResponseEntity<PagedModel<EntityModel<SavedMessageDTO>>> getSavedMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            PagedResourcesAssembler<SavedMessageDTO> pagedAssembler) {

        Page<SavedMessageDTO> messagesPage = messageService
                .getSavedMessagesHistory(page, size);

        PagedModel<EntityModel<SavedMessageDTO>> pagedModel
                = pagedAssembler.toModel(messagesPage);

        return ResponseEntity.ok(pagedModel);
    }
}
