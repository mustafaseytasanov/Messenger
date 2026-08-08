package ru.mustafa.messenger.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mustafa.messenger.model.Message;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for managing {@link Message} entities.
 *
 * @author Mustafa
 * @version 1.1.
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // Cursor Pagination for messages
    @EntityGraph(attributePaths = {"author"})
    Window<Message> findByChatId(
            long chatId, ScrollPosition scrollPosition, Sort sort,
            Limit limit);

    // Pagination for "Saved" messages
    Page<Message> findByChatId(Long chatId, Pageable pageable);

    long countByCreatedAtAfter(LocalDateTime startOfDay);

}
