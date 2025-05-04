package backend.academy.scrapper.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "link")
@NoArgsConstructor
@AllArgsConstructor
public class LinkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String url;

    @Column(name = "last_updated_at")
    private Instant lastUpdatedAt;

    @Column(name = "last_checked_at")
    private Instant lastCheckedAt;

    @ManyToMany(mappedBy = "links")
    private Set<ChatEntity> chats = new HashSet<>();

}
