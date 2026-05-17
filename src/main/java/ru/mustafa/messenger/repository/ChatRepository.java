package ru.mustafa.messenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mustafa.messenger.model.Chat;
import ru.mustafa.messenger.model.User;

/**
 * Repository interface for managing {@link Chat} entities.
 *
 * @author Mustafa
 * @version 1.0.
 */
@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
}
