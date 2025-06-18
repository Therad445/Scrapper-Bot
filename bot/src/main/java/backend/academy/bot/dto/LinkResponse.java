package backend.academy.bot.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class LinkResponse {

    private Long id;
    private URI  link;
    private List<String> tags    = new ArrayList<>();
    private List<String> filters = new ArrayList<>();

    public LinkResponse() {}

    @JsonCreator
    public LinkResponse(
        @JsonProperty("id")      Long id,
        @JsonProperty("link")    URI  link,
        @JsonProperty("tags")    List<String> tags,
        @JsonProperty("filters") List<String> filters) {
        this.id      = id;
        this.link    = link;
        this.tags    = tags  != null ? tags    : new ArrayList<>();
        this.filters = filters != null ? filters : new ArrayList<>();
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
