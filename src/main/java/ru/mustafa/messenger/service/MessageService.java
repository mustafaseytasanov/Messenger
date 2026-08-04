package ru.mustafa.messenger.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mustafa.messenger.dto.*;
import ru.mustafa.messenger.exception.ChatAccessDeniedException;
import ru.mustafa.messenger.exception.DuplicateRequestException;
import ru.mustafa.messenger.exception.ResourceNotFoundException;
import ru.mustafa.messenger.model.Chat;
import ru.mustafa.messenger.model.Message;
import ru.mustafa.messenger.model.User;
import ru.mustafa.messenger.repository.ChatRepository;
import ru.mustafa.messenger.repository.MessageRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Service responsible for creating new chat messages and retrieving message history.
 *
 * @author Mustafa
 * @version 1.2
 */
@Service
@RequiredArgsConstructor
public class MessageService {

    private final ChatRepository chatRepository;
    private final UserService userService;
    private final MessageRepository messageRepository;
    private final StringRedisTemplate redisTemplate;
    private final NotificationService notificationService;

    /**
     * Creates and saves a new message within a specific chat room.
     *
     * @param messageDTO the data container for the new message
     * @return the unique identifier of the saved message
     * @throws ResourceNotFoundException if the specified chat room does not exist
     * @throws ChatAccessDeniedException if user is not a participant of the chat
     */
    @Transactional
    public long createMessage(String idempotencyKey, MessageDTO messageDTO) {
        checkAndLockIdempotencyKey(idempotencyKey);

        try {
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

            // Sending notification
            Set<User> users = chat.getUsers();
            users.remove(currentUser);
            for (User user : users) {
                notificationService.sendNotificationToUser(
                        currentUser.getUsername(), user.getId(),
                        message.getText());
            }

            // Updating status in Redis
            redisTemplate.opsForValue().set(idempotencyKey, "SUCCESS", 5,
                    TimeUnit.MINUTES);

            return message.getId();

        } catch (Exception e) {
            redisTemplate.delete(idempotencyKey);
            throw e;
        }
    }

    // Creating saved message
    @CacheEvict(value = "saved_messages", key = "#userId")
    @Transactional
    public long createSavedMessage(
            String idempotencyKey,
            RequestSavedMessageDTO requestSavedMessageDTO,
            Long userId) {

        checkAndLockIdempotencyKey(idempotencyKey);

        try {
            User currentUser = userService.getCurrentUser();
            String name = "saved_" + currentUser.getUsername();
            Chat chat = chatRepository.findByName(name)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Saved Chat not found for user: "
                                    + currentUser.getUsername()));

            Message message = new Message();
            message.setChat(chat);
            message.setAuthor(currentUser);
            message.setText(requestSavedMessageDTO.text());
            message.setCreatedAt(LocalDateTime.now());
            message = messageRepository.save(message);

            // Updating status in Redis
            redisTemplate.opsForValue().set(idempotencyKey, "SUCCESS", 5,
                    TimeUnit.MINUTES);

            return message.getId();

        } catch (Exception e) {
            redisTemplate.delete(idempotencyKey);
            throw e;
        }
    }

    private void checkAndLockIdempotencyKey(String key) {
        // setIfAbsent will succeed (true) only if the key is not yet in Redis
        Boolean isFirstRequest = redisTemplate.opsForValue()
                .setIfAbsent(key, "PROCESSING", 5, TimeUnit.MINUTES);

        if (Boolean.FALSE.equals(isFirstRequest)) {
            throw new DuplicateRequestException(
                    "This message has already been sent or is being processed.");
        }
    }

    /**
     * Retrieves all messages belonging to a chat, ordered from earliest to latest.
     * Second approach of pagination.
     *
     * @param chatId the unique identifier of the chat room
     * @return a chronologically sorted list of chat messages converted to data transfer objects
     * @throws ResourceNotFoundException if the chat room does not exist
     * @throws ChatAccessDeniedException if the current authenticated user is not a participant of the chat
     */
    @Transactional(readOnly = true)
    public ChatMessagesResponse getChatMessages(
            long chatId, String cursorToken, int size) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Chat not found with id: " + chatId));

        User user = userService.getCurrentUser();
        boolean isParticipant = chatRepository.isUserParticipant(chatId,
                user.getId());
        if (!isParticipant) {
            throw new ChatAccessDeniedException("You're not a participant of this chat");
        }

        Sort sort = Sort.by(Sort.Direction.ASC, "createdAt")
                .and(Sort.by(Sort.Direction.ASC, "id"));

        // First page
        ScrollPosition position = ScrollPosition.keyset();

        if (cursorToken != null && !cursorToken.isBlank()) {
            try {
                String decoded = new String(
                        Base64.getDecoder().decode(cursorToken),
                        StandardCharsets.UTF_8);

                int lastColonIndex = decoded.lastIndexOf(":");
                String createdAtStr = decoded.substring(0, lastColonIndex);
                String idStr = decoded.substring(lastColonIndex + 1);

                LocalDateTime createdAt = LocalDateTime.parse(createdAtStr);
                Long id = Long.parseLong(idStr);

                Map<String, Object> keys = new LinkedHashMap<>();
                keys.put("createdAt", createdAt);
                keys.put("id", id);

                position = ScrollPosition.forward(keys);
            } catch (Exception e) {
                position = ScrollPosition.keyset();
            }
        }

        Window<Message> messageWindow = messageRepository
                .findByChatId(chatId, position, sort, Limit.of(size));

        List<ChatMessagesDTO> dtoList = messageWindow.getContent().stream()
                .map(msg -> new ChatMessagesDTO(
                        msg.getAuthor().getUsername(),
                        msg.getText(),
                        msg.getCreatedAt()
                ))
                .toList();

        // Coding token for a next page
        String nextCursorToken = null;
        if (!messageWindow.isEmpty() && messageWindow.hasNext()) {
            ScrollPosition nextPosition = messageWindow
                    .positionAt(messageWindow.size() - 1);

            if (nextPosition instanceof KeysetScrollPosition keyset) {
                String createdAtVal = keyset.getKeys().get("createdAt").toString();
                String idVal = keyset.getKeys().get("id").toString();

                String rawToken = createdAtVal + ":" + idVal;

                nextCursorToken = Base64.getEncoder().encodeToString(
                        rawToken.getBytes(StandardCharsets.UTF_8));
            }
        }

        return new ChatMessagesResponse(dtoList, nextCursorToken, messageWindow.hasNext());

    }

    // First approach of pagination.
    @Cacheable(value = "saved_messages", key = "#userId")
    public Page<SavedMessageDTO> getSavedMessagesHistory(
            Long userId, int page, int size) {

        String username = userService.getCurrentUser().getUsername();
        String chatName = "saved_" + username;

        Chat savedChat = chatRepository.findByName(chatName)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Чат Избранное не найден для пользователя: "
                                + username));

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());

        return messageRepository.findByChatId(savedChat.getId(), pageable)
                .map(msg -> new SavedMessageDTO(
                        msg.getText(),
                        msg.getCreatedAt()
                ));
    }
}
