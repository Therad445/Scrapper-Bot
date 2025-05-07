package backend.academy.bot.dto;

import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.Collections;
import java.util.Set;
import org.hibernate.validator.constraints.URL;

public class LinkUpdate {
    @NotNull
    private final Long id;

    @URL
    @NotNull
    private final URI url;

    @NotNull
    private final String description;

    @NotNull
    private final Set<Long> tgChatIds;

    public LinkUpdate(Long id, URI url, String description, Set<Long> tgChatIds) {
        this.id = id;
        this.url = url;
        this.description = description;
        this.tgChatIds = tgChatIds != null ? Set.copyOf(tgChatIds) : Collections.emptySet();
    }

    public Long getId() {
        return id;
    }

    public URI getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public Set<Long> getTgChatIds() {
        return tgChatIds;
    }
}
