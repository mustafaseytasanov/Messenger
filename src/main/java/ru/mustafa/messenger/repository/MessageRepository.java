package ru.mustafa.messenger.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mustafa.messenger.model.Message;

import java.util.List;

/**
 * Repository interface for managing {@link Message} entities.
 *
 * @author Mustafa
 * @version 1.0.
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @EntityGraph(attributePaths = {"author"})
    List<Message> findByChatIdOrderByCreatedAtAsc(long chatId);
}
