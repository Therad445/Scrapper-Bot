package backend.academy.scrapper.repository;

import backend.academy.scrapper.model.TrackedLink;
import java.util.List;

public interface TrackedLinkRepository {
    List<TrackedLink> findAll();
    void save(TrackedLink trackedLink);
    void update(TrackedLink trackedLink);
    void delete(String url);
}
