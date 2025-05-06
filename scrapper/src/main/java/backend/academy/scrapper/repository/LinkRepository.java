package backend.academy.scrapper.repository;

import backend.academy.scrapper.model.LinkInfo;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LinkRepository {
    void add(long chatId, String url,
             Set<String> tags,
             Set<String> filters);

    void addTag(long chatId, long linkId, String tag);

    void removeTag(long chatId, long linkId, String tag);

    void addFilter(long chatId, long linkId, String filter);

    void removeFilter(long chatId, long linkId, String filter);

    Optional<LinkInfo> remove(long chatId, String url);

    Page<LinkInfo> findLinksForCheck(Instant threshold, Pageable pageable);

    void updateCheckTime(long linkId, Instant checkedAt, Instant updatedAt);

    List<LinkInfo> findAllByChat(long chatId);
}
