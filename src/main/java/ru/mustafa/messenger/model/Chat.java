package ru.mustafa.messenger.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Class Chat.
 *
 * @author Mustafa
 * @version 1.0.
 */
@Entity
@Table(name = "chats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "chat")
    private Set<Message> messages = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "chats_users",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users = new HashSet<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Method that compare 2 objects.
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
        Chat chat = (Chat) obj;
        return id != null
                && id.equals(chat.getId())
                && Objects.equals(name, chat.getName())
                && Objects.equals(createdAt, chat.getCreatedAt());
    }

    /**
     * Method that calculates object hash code.
     *
     * @return hash code.
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Method toString().
     * @return Object in string expression.
     */
    @Override
    public String toString() {
        return "Chat{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", createdAt=" + createdAt
                + '}';
    }
}
