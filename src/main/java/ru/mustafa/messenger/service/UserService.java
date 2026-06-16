package ru.mustafa.messenger.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mustafa.messenger.dto.UserRegisteredEvent;
import ru.mustafa.messenger.model.User;
import ru.mustafa.messenger.model.Role;
import ru.mustafa.messenger.repository.UserRepository;

/**
 * Service for managing user accounts, profiles, and Spring Security user details retrieval.
 *
 * @author Mustafa
 * @version 1.1
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Saves a user entity directly to the database.
     *
     * @param user the user entity to save
     * @return the saved user entity
     */
    @Transactional
    public User save(User user) {
        return repository.save(user);
    }


    /**
     * Creates and provisions a new user, ensuring uniqueness of username and email.
     *
     * @param user the user entity definition to register
     * @return the newly created and saved user entity
     * @throws RuntimeException if the username or email address is already registered
     */
    @Transactional
    public User create(User user) {
        if (repository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }

        if (repository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        User savedUser = save(user);

        eventPublisher.publishEvent(new UserRegisteredEvent(
                savedUser.getId(), savedUser.getUsername()));

        return savedUser;
    }

    /**
     * Retrieves a user entity based on their unique username string.
     *
     * @param username the username to search for
     * @return the located user entity
     * @throws UsernameNotFoundException if no user is found matching the given username
     */
    @Transactional(readOnly = true)
    public User getByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

    }

    /**
     * Provides an implementation of {@link UserDetailsService} for Spring Security using a method reference.
     *
     * @return the UserDetailsService functional interface instance
     */
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    /**
     * Fetches the user entity corresponding to the currently authenticated principal in the security context.
     *
     * @return the current authenticated user entity
     */
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        // Getting the username from the Spring Security context
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }


    /**
     * Grants the administrative role to the currently authenticated user.
     * <p>
     * This method is intended strictly for demonstration and testing purposes.
     * </p>
     *
     * @deprecated replaced by proper database-driven or administrative panel assignment
     */
    @Deprecated
    @Transactional
    public void getAdmin() {
        var user = getCurrentUser();
        user.setRole(Role.ROLE_ADMIN);
        save(user);
    }
}
