package ru.mustafa.messenger.service;

import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    @Async("notificationExecutor")
    public void sendNotificationToUser(String username,
                                       Long userId,
                                       String messageText) {

        String payload = "username: " + messageText;

        // Spring will send this to: /user/{userId}/queue/messages
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/messages",
                payload
        );
    }
}