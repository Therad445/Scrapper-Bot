package backend.academy.scrapper.model;

import java.util.Set;

public class LinkResponse {
    private final Long id;
    private final String link;
    private final Set<String> tags;
    private final Set<String> filters;

    public LinkResponse(Long id, String link, Set<String> tags, Set<String> filters) {
        this.id = id;
        this.link = link;
        this.tags = tags;
        this.filters = filters;
    }

    public Long getId() {
        return id;
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
