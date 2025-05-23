package backend.academy.scrapper.model;

import jakarta.validation.constraints.NotBlank;
import java.util.Set;
import org.hibernate.validator.constraints.URL;

public record AddLinkRequest(@URL @NotBlank String link, Set<String> tags, Set<String> filters) {
    public AddLinkRequest(String link, Set<String> tags, Set<String> filters) {
        this.link = link;
        this.tags = tags != null ? tags : Set.of();
        this.filters = filters != null ? filters : Set.of();
    }
}
