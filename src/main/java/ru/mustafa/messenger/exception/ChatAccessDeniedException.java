package ru.mustafa.messenger.exception;

import java.io.Serial;

/**
 * The exception that is thrown when access is denied.
 *
 * @author Mustafa
 * @version 1.0
 */
public class ChatAccessDeniedException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Exception constructor with detailed message.
     *
     * @param message description of the cause of the error.
     */
    public ChatAccessDeniedException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with a detail message and the cause.
     *
     * @param message the detail message.
     * @param cause   the cause of the exception (can be null).
     */
    public ChatAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}