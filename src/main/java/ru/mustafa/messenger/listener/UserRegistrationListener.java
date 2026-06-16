package ru.mustafa.messenger.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import ru.mustafa.messenger.dto.UserRegisteredEvent;
import ru.mustafa.messenger.service.ChatService;

@Component
@RequiredArgsConstructor
public class UserRegistrationListener {

    private final ChatService chatService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserRegistration(UserRegisteredEvent event) {

        chatService.createSavedChat("saved_" + event.username(),
                event.userId());
    }
}
