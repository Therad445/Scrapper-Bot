package backend.academy.scrapper.model;

import java.util.Set;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

public class AddLinkRequest {
    @URL
    @NotBlank
    private final String link;
    @NotEmpty
    private final Set<String> tags;
    @NotEmpty
    private final Set<String> filters;

    public AddLinkRequest(String link, Set<String> tags, Set<String> filters) {
        this.link = link;
        this.tags = tags;
        this.filters = filters;
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
