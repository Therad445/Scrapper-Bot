package backend.academy.bot.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


public class LinkResponse {
    private Long id;
    private String url;
    private List<String> tags;
    private List<String> filters;

    public LinkResponse() {}

    public LinkResponse(Long id, String url, List<String> tags, List<String> filters) {
        this.id = id;
        this.url = url;
        this.tags = tags;
        this.filters = filters;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
