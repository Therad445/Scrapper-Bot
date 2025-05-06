package backend.academy.scrapper.entity;

import static jakarta.persistence.FetchType.LAZY;

import backend.academy.scrapper.model.SubscriptionTagId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
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
@Table(name = "subscription_tag")
public class SubscriptionTagEntity {

    @EmbeddedId
    private SubscriptionTagId id = new SubscriptionTagId();

    @MapsId("chatId")
    @ManyToOne
    @JoinColumn(name = "chat_id")
    private ChatEntity chat;

    @MapsId("linkId")
    @ManyToOne
    @JoinColumn(name = "link_id")
    private LinkEntity link;

    @MapsId("tagId")
    @ManyToOne
    @JoinColumn(name = "tag_id")
    private TagEntity tag;

    @ManyToOne(fetch = LAZY)
    @JoinColumns({
        @JoinColumn(name = "chat_id", referencedColumnName = "chat_id", insertable = false, updatable = false),
        @JoinColumn(name = "link_id", referencedColumnName = "link_id", insertable = false, updatable = false)
    })
    private SubscriptionEntity subscription;
}
