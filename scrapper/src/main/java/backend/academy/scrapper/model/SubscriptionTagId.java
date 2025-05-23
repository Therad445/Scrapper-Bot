package backend.academy.scrapper.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionTagId implements Serializable {
    @Column(name = "tag_id")
    private Long tagId;

    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "link_id")
    private Long linkId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SubscriptionTagId that = (SubscriptionTagId) o;
        return Objects.equals(tagId, that.tagId)
                && Objects.equals(chatId, that.chatId)
                && Objects.equals(linkId, that.linkId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagId, chatId, linkId);
    }
}
