package backend.academy.scrapper.model;

import java.util.Set;

public record LinkInfo(long id, String url, Set<String> tags, Set<String> filters) {
}
