package ru.mustafa.messenger.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mustafa.messenger.dto.ChatMessagesDTO;
import ru.mustafa.messenger.dto.MessageDTO;
import ru.mustafa.messenger.dto.SavedMessageDTO;
import ru.mustafa.messenger.exception.ChatAccessDeniedException;
import ru.mustafa.messenger.exception.ResourceNotFoundException;
import ru.mustafa.messenger.model.Chat;
import ru.mustafa.messenger.model.Message;
import ru.mustafa.messenger.model.User;
import ru.mustafa.messenger.repository.ChatRepository;
import ru.mustafa.messenger.repository.MessageRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service responsible for creating new chat messages and retrieving message history.
 *
 * @author Mustafa
 * @version 1.1
 */
@Service
@RequiredArgsConstructor
public class MessageService {

    private final ChatRepository chatRepository;
    private final UserService userService;
    private final MessageRepository messageRepository;

    /**
     * Creates and saves a new message within a specific chat room.
     *
     * @param messageDTO the data container for the new message
     * @return the unique identifier of the saved message
     * @throws ResourceNotFoundException if the specified chat room does not exist
     * @throws ChatAccessDeniedException if user is not a participant of the chat
     */
    @Transactional
    public long createMessage(MessageDTO messageDTO) {

        Chat chat = chatRepository.findById(messageDTO.chatId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Chat not found with id: "
                                + messageDTO.chatId()));

        User currentUser = userService.getCurrentUser();
        boolean isParticipant = chatRepository.isUserParticipant(chat.getId(),
                currentUser.getId());
        if (!isParticipant) {
            throw new ChatAccessDeniedException("You're not a participant of this chat");
        }

        Message message = new Message();
        message.setChat(chat);
        message.setAuthor(currentUser);
        message.setText(messageDTO.text());
        message.setCreatedAt(LocalDateTime.now());
        message = messageRepository.save(message);
        return message.getId();
    }

    /**
     * Retrieves all messages belonging to a chat, ordered from earliest to latest.
     *
     * @param chatId the unique identifier of the chat room
     * @return a chronologically sorted list of chat messages converted to data transfer objects
     * @throws ResourceNotFoundException if the chat room does not exist
     * @throws ChatAccessDeniedException        if the current authenticated user is not a participant of the chat
     */
    @Transactional(readOnly = true)
    public List<ChatMessagesDTO> getChatMessages(long chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Chat not found with id: " + chatId));

        User user = userService.getCurrentUser();
        boolean isParticipant = chatRepository.isUserParticipant(chatId,
                user.getId());
        if (!isParticipant) {
            throw new ChatAccessDeniedException("You're not a participant of this chat");
        }

        List<Message> chatMessages = messageRepository
                .findByChatIdOrderByCreatedAtAsc(chatId);
        return chatMessages.stream()
                .map(msg -> new ChatMessagesDTO(
                        msg.getAuthor().getUsername(),
                        msg.getText(),
                        msg.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    // First approach of pagination.
    public Page<SavedMessageDTO> getSavedMessagesHistory(int page, int size) {

        String username = userService.getCurrentUser().getUsername();
        String chatName = "saved_" + username;

        Chat savedChat = chatRepository.findByName(chatName)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Чат Избранное не найден для пользователя: "
                                + username));

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());

        // 4. Запрашиваем страницу сообщений и маппим в DTO
        return messageRepository.findByChatId(savedChat.getId(), pageable)
                .map(msg -> new SavedMessageDTO(
                        msg.getText(),
                        msg.getCreatedAt()
                ));
    }
}
