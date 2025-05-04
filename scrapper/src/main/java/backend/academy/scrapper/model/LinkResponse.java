package backend.academy.scrapper.model;

import java.util.Set;

public record LinkResponse(Long id, String link, Set<String> tags, Set<String> filters) {
}
