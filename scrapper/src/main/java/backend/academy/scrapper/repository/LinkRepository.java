package backend.academy.scrapper.repository;

import backend.academy.scrapper.model.LinkInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface LinkRepository {
    void add(long chatId, String url);
    Optional<LinkInfo> remove(long chatId, String url);
    Page<LinkInfo> findLinksForCheck(Instant threshold, Pageable pageable);
    void updateCheckTime(long linkId, Instant checkedAt, Instant updatedAt);
    List<LinkInfo> findAllByChat(long chatId);
}
