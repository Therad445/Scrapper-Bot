package backend.academy.bot.dto;

import java.util.ArrayList;
import java.util.List;

public class ListLinksResponse {
    private final List<LinkResponse> links = new ArrayList<>();
    private int size;

    public ListLinksResponse() {
    }

    public List<LinkResponse> getLinks() {
        return links;
    }

    public int getSize() {
        return size;
    }
}
