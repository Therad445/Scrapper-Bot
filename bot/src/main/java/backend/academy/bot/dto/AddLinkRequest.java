package backend.academy.bot.dto;

import java.util.ArrayList;
import java.util.List;

public class AddLinkRequest {
    private String link;
    private List<String> tags = new ArrayList<>();
    private List<String> filters = new ArrayList<>();

    public AddLinkRequest() {
    }

    public AddLinkRequest(String link, List<String> tags, List<String> filters) {
        this.link = link;
        this.tags = tags;
        this.filters = filters;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
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
