package backend.academy.bot.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Collections;
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

    public LinkUpdate() {}

    public LinkUpdate(Long id, String url, String description, Set<Long> tgChatIds) {
        this.id = id;
        this.url = url;
        this.description = description;
        this.tgChatIds = tgChatIds != null ? Set.copyOf(tgChatIds) : Collections.emptySet();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Long> getTgChatIds() {
        return tgChatIds;
    }

    public void setTgChatIds(Set<Long> tgChatIds) {
        this.tgChatIds = tgChatIds;
    }
}
