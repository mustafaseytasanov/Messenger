package ru.mustafa.messenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mustafa.messenger.model.Message;

/**
 * Repository interface for managing {@link Message} entities.
 *
 * @author Mustafa
 * @version 1.0.
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
}
