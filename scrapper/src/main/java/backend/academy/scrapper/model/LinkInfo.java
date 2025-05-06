package backend.academy.scrapper.model;


import java.time.Instant;
import java.util.Set;

public record LinkInfo(
    long id,
    String url,
    Instant lastCheckedAt,
    Instant lastUpdatedAt,
    Set<String> tags,
    Set<String> filters) {
}
