package backend.academy.scrapper.repository;

import backend.academy.scrapper.model.TrackedLink;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class inMemoryTrackerLinkRepository implements TrackedLinkRepository {
    private final List<TrackedLink> trackedLinks = new ArrayList<>();

    @Override
    public List<TrackedLink> findAll() {
        return new ArrayList<>(trackedLinks);
    }

    @Override
    public void save(TrackedLink trackedLink) {
        trackedLinks.add(trackedLink);
    }

    @Override
    public void update(TrackedLink trackedLink) {
        Optional<TrackedLink> existing = trackedLinks.stream()
            .filter(link -> link.url().equals(trackedLink.url()))
            .findFirst();
        existing.ifPresent(link -> link.lastUpdate(trackedLink.lastUpdate()));
    }

    @Override
    public void delete(String url) {
        trackedLinks.removeIf(link -> link.url().equals(url));
    }
}
