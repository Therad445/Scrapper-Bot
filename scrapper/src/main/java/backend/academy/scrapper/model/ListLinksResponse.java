package backend.academy.scrapper.model;

import java.util.List;

public class ListLinksResponse {
    private final List<LinkResponse> links;
    private final int size;

    public ListLinksResponse(List<LinkResponse> links, int size) {
        this.links = links;
        this.size = size;
    }

    public List<LinkResponse> getLinks() {
        return links;
    }

    public int getSize() {
        return size;
    }
}
