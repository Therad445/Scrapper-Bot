package backend.academy.scrapper.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "chat")
@NoArgsConstructor
@AllArgsConstructor
public class ChatEntity {

    @Id
    private Long id;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToMany
    @JoinTable(
        name = "subscription",
        joinColumns = @JoinColumn(name = "chat_id"),
        inverseJoinColumns = @JoinColumn(name = "link_id")
    )
    private Set<LinkEntity> links = new HashSet<>();

    public ChatEntity(Long id, Instant createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

}
