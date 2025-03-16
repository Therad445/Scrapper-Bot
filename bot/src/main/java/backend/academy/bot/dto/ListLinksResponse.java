package backend.academy.bot.dto;

import java.util.List;

public class ListLinksResponse {
    private List<LinkResponse> links;
    private int size;

    public ListLinksResponse() {}

    public ListLinksResponse(List<LinkResponse> links, int size) {
        this.links = links;
        this.size = size;
    }

    public List<LinkResponse> getLinks() {
        return links;
    }

    public void setLinks(List<LinkResponse> links) {
        this.links = links;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
