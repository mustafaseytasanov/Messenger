package ru.mustafa.messenger.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mustafa.messenger.dto.ChatDTO;
import ru.mustafa.messenger.dto.UserChatsDTO;
import ru.mustafa.messenger.model.Chat;
import ru.mustafa.messenger.model.Message;
import ru.mustafa.messenger.model.User;
import ru.mustafa.messenger.repository.ChatRepository;
import ru.mustafa.messenger.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service for managing user chats and chat creation logic.
 *
 * @author Mustafa
 * @version 1.0.
 */
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    /**
     * Creates a new chat room and assigns users to it along with the creator.
     *
     * @param chatDTO the data container with chat name and participant IDs
     * @return the unique identifier of the newly created chat
     * @throws RuntimeException if any of the provided user IDs are not found
     */
    @Transactional
    public long createChat(ChatDTO chatDTO) {

        Chat chat = new Chat();
        chat.setName(chatDTO.name());
        Set<User> users = new HashSet<>();
        Optional<User> optionalUser;
        for (long chatId: chatDTO.users()) {
            optionalUser = userRepository.findById(chatId);
            if (optionalUser.isEmpty()) {
                throw new RuntimeException("User not found.");
            }
            users.add(optionalUser.get());
        }
        users.add(userService.getCurrentUser());
        chat.setUsers(users);
        chat.setCreatedAt(LocalDateTime.now());
        chat = chatRepository.save(chat);
        return chat.getId();
    }

    /**
     * Retrieves all chats for the currently authenticated user,
     * sorted by the timestamp of the last message in descending order.
     *
     * @return a sorted list of data transfer objects representing user chats
     */
    @Transactional(readOnly = true)
    public List<UserChatsDTO> getUserChats() {
        User user = userService.getCurrentUser();
        Set<Chat> userChats = user.getChats();
        Map<Chat, LocalDateTime> chatAndLatestMessageMap = new HashMap<>();
        for (Chat chat: userChats) {
            Set<Message> messages = chat.getMessages();
            messages.stream()
                    .map(Message::getCreatedAt)
                    .max(Comparator.naturalOrder())
                    .ifPresent(maxCreatedAt ->
                            chatAndLatestMessageMap.put(chat, maxCreatedAt));
        }

        return chatAndLatestMessageMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .map(entry -> new UserChatsDTO(
                        entry.getKey().getName(),
                        entry.getValue()
                ))
                .toList();
    }
}
