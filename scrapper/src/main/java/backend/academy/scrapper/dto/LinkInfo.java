package backend.academy.scrapper.dto;

import java.util.HashSet;
import java.util.Set;

public class LinkInfo {
    private final String link;
    private final Set<String> tags;
    private final Set<String> filters;

    public LinkInfo(String link, Set<String> tags, Set<String> filters) {
        this.link = link;
        this.tags = tags == null ? new HashSet<>() : tags;
        this.filters = filters == null ? new HashSet<>() : filters;
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
