package backend.academy.bot.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Set;
import org.hibernate.validator.constraints.URL;

public class LinkUpdate {
    @NotNull
    private final Long id;

    @URL
    @NotNull
    private final String url;

    @NotNull
    private final String description;

    @NotNull
    private final Set<Long> tgChatIds;

    public LinkUpdate(Long id, String url, String description, Set<Long> tgChatIds) {
        this.id = id;
        this.url = url;
        this.description = description;
        this.tgChatIds = tgChatIds != null ? Set.copyOf(tgChatIds) : Collections.emptySet();
    }

    public Long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public Set<Long> getTgChatIds() {
        return tgChatIds;
    }
}
