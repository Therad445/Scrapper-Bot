package backend.academy.scrapper.model;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import java.util.List;

@Setter
@Getter
public class LinkUpdate {
    public LinkUpdate() {
    }

    @Positive
    private Long id;
    @URL
    private String url;
    private String description;
    private List<Long> tgChatIds; // Список Telegram ID, кому отправлять уведомление

    public LinkUpdate(Long id, String url, String description, List<Long> tgChatIds) {
        this.id = id;
        this.url = url;
        this.description = description;
        this.tgChatIds = tgChatIds;
    }
}
