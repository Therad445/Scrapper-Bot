package backend.academy.bot.dto;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class AddLinkRequest {
    private URI link;
    private List<String> tags = new ArrayList<>();
    private List<String> filters = new ArrayList<>();

    public AddLinkRequest() {}

    public AddLinkRequest(URI link, List<String> tags, List<String> filters) {
        this.link = link;
        this.tags = tags;
        this.filters = filters;
    }

    public URI getLink() {
        return link;
    }

    public void setLink(URI link) {
        this.link = link;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getFilters() {
        return filters;
    }

    public void setFilters(List<String> filters) {
        this.filters = filters;
    }
}
