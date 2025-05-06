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
public class SubscriptionId implements Serializable {

    @Column(name = "chat_id")
    private Long chatId;
    @Column(name = "link_id")
    private Long linkId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubscriptionId that)) return false;
        return Objects.equals(chatId, that.chatId) &&
            Objects.equals(linkId, that.linkId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, linkId);
    }
}
