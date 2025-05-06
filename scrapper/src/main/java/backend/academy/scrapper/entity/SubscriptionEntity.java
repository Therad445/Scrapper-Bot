package backend.academy.scrapper.entity;

import backend.academy.scrapper.model.SubscriptionId;
import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "subscription")
public class SubscriptionEntity {

    @EmbeddedId
    private SubscriptionId id = new SubscriptionId();

    @MapsId("chatId")
    @ManyToOne
    @JoinColumn(name = "chat_id")
    private ChatEntity chat;

    @MapsId("linkId")
    @ManyToOne
    @JoinColumn(name = "link_id")
    private LinkEntity link;

    @OneToMany(mappedBy = "subscription",
        cascade = CascadeType.ALL,
        orphanRemoval = true)
    private Set<SubscriptionTagEntity> tags = new HashSet<>();

    @OneToMany(mappedBy = "subscription",
        cascade = CascadeType.ALL,
        orphanRemoval = true)
    private Set<SubscriptionFilterEntity> filters = new HashSet<>();
}

