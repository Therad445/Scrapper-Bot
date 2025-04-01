package backend.academy.scrapper.model;

import jakarta.validation.constraints.NotBlank;
import java.util.Set;
import org.hibernate.validator.constraints.URL;

public class AddLinkRequest {
    @URL
    @NotBlank
    private final String link;

    private final Set<String> tags;

    private final Set<String> filters;

    public AddLinkRequest(String link, Set<String> tags, Set<String> filters) {
        this.link = link;
        this.tags = tags != null ? tags : Set.of();
        this.filters = filters != null ? filters : Set.of();
    }

    public String getLink() {
        return link;
    }

    public Set<String> getTags() {
        return tags;
    }

    public Set<String> getFilters() {
        return filters;
    }
}
