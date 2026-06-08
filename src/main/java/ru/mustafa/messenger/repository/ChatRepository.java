package ru.mustafa.messenger.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mustafa.messenger.model.Chat;
import ru.mustafa.messenger.model.User;

import java.util.List;

/**
 * Repository interface for managing {@link Chat} entities.
 *
 * @author Mustafa
 * @version 1.1
 */
@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    @EntityGraph(attributePaths = {"messages"})
    List<Chat> findByUsersId(@Param("userId") Long userId);

    @Query("SELECT COUNT(c) > 0 FROM Chat c JOIN c.users u " +
            "WHERE c.id = :chatId AND u.id = :userId")
    boolean isUserParticipant(@Param("chatId") Long chatId,
                              @Param("userId") Long userId);
}
