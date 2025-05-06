package backend.academy.bot.dto;

import java.util.ArrayList;
import java.util.List;

public class LinkResponse {
    private Long id;
    private String link;
    private List<String> tags = new ArrayList<>();
    private List<String> filters = new ArrayList<>();

    public LinkResponse() {
    }

    public LinkResponse(Long id, String link, List<String> tags, List<String> filters) {
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

    public List<String> getTags() {
        return tags;
    }

    public List<String> getFilters() {
        return filters;
    }
}
