package backend.academy.scrapper.entity;

import backend.academy.scrapper.model.SubscriptionFilterId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "subscription_filter")
public class SubscriptionFilterEntity {

    @EmbeddedId
    private SubscriptionFilterId id = new SubscriptionFilterId();

    @MapsId("chatId")
    @ManyToOne
    @JoinColumn(name = "chat_id")
    private ChatEntity chat;

    @MapsId("linkId")
    @ManyToOne
    @JoinColumn(name = "link_id")
    private LinkEntity link;

    @MapsId("filterId")
    @ManyToOne
    @JoinColumn(name = "filter_id")
    private FilterEntity filter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "chat_id", referencedColumnName = "chat_id", insertable = false, updatable = false),
        @JoinColumn(name = "link_id", referencedColumnName = "link_id", insertable = false, updatable = false)
    })
    private SubscriptionEntity subscription;
}
