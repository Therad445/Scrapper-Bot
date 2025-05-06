package backend.academy.scrapper.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "chat")
@Getter
@Setter
@NoArgsConstructor
public class ChatEntity {

    @Id
    private Long id;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "chat",
        cascade = CascadeType.ALL,
        orphanRemoval = true)
    private Set<SubscriptionEntity> subscriptions = new HashSet<>();

    public ChatEntity(Long id, Instant createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }
}
