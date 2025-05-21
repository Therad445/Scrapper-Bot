package backend.academy.bot.dto;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class LinkResponse {
    private final Long id;
    private final URI link;
    private List<String> tags = new ArrayList<>();
    private List<String> filters = new ArrayList<>();

    public LinkResponse(Long id, URI link, List<String> tags, List<String> filters) {
        this.id = id;
        this.link = link;
        this.tags = tags;
        this.filters = filters;
    }

    public Long getId() {
        return id;
    }

    public URI getLink() {
        return link;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<String> getFilters() {
        return filters;
    }
}
