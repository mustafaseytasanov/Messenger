package ru.mustafa.messenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mustafa.messenger.model.User;

import java.util.Optional;

/**
 * Repository interface for managing {@link User} entities.
 *
 * @author Mustafa
 * @version 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their unique username string.
     *
     * @param username the username to search for
     * @return an Optional containing the found user, or empty if not found
     */
    Optional<User> findByUsername(String username);
    /**
     * Checks if a user already exists with the given username.
     *
     * @param username the username to check
     * @return true if the username is taken, false otherwise
     */
    boolean existsByUsername(String username);
    /**
     * Checks if a user already exists with the given email address.
     *
     * @param email the email address to check
     * @return true if the email is taken, false otherwise
     */
    boolean existsByEmail(String email);
}
