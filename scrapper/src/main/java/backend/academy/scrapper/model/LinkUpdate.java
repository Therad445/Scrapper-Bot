package backend.academy.scrapper.model;

import jakarta.validation.constraints.NotNull;
import java.util.Set;
import org.hibernate.validator.constraints.URL;

public class LinkUpdate {
    @NotNull
    private Long id;

    @URL
    @NotNull
    private String url;

    @NotNull
    private String description;

    @NotNull
    private Set<Long> tgChatIds;

    public LinkUpdate(Long id, String url, String description, Set<Long> tgChatIds) {
        this.id = id;
        this.url = url;
        this.description = description;
        this.tgChatIds = tgChatIds;
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
