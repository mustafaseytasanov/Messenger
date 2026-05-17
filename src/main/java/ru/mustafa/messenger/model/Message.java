package ru.mustafa.messenger.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Class Message.
 *
 * @author Mustafa
 * @version 1.0.
 */
@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Method that compares 2 objects based on identifier, text, and creation time.
     *
     * @param obj input object.
     * @return true if objects are equal else false.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Message message = (Message) obj;
        return id != null
                && id.equals(message.getId())
                && Objects.equals(text, message.getText())
                && Objects.equals(createdAt, message.getCreatedAt());
    }

    /**
     * Method that calculates object hash code using class type to prevent collection corruption.
     *
     * @return hash code.
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Method toString().
     *
     * @return Object in string expression.
     */
    @Override
    public String toString() {
        return "Message{"
                + "id=" + id
                + ", text='" + text + '\''
                + ", createdAt=" + createdAt
                + '}';
    }
}
