package backend.academy.scrapper.repository.sql;

import backend.academy.scrapper.model.LinkInfo;
import backend.academy.scrapper.repository.LinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@ConditionalOnProperty(name = "access-type", havingValue = "SQL")
public class SqlLinkRepository implements LinkRepository {
    private final JdbcTemplate jdbc;
    public SqlLinkRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void add(long chatId, String url) {
        Long linkId;
        try {
            linkId = jdbc.queryForObject(
                "SELECT id FROM link WHERE url = ?",
                Long.class,
                url
            );
        } catch (EmptyResultDataAccessException ex) {
            linkId = null;
        }

        if (linkId == null) {
            linkId = jdbc.queryForObject(
                "INSERT INTO link(url, last_checked_at) VALUES (?, ?) RETURNING id",
                Long.class,
                url,
                Timestamp.from(Instant.EPOCH)
            );
        }

        jdbc.update(
            "INSERT INTO subscription(chat_id, link_id) VALUES (?, ?) ON CONFLICT DO NOTHING",
            chatId,
            linkId
        );
    }


    @Override
    public Optional<LinkInfo> remove(long chatId, String url) {
        Long linkId;
        try {
            linkId = jdbc.queryForObject(
                "SELECT id FROM link WHERE url = ?", Long.class, url
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

        if (linkId == null) return Optional.empty();

        int rows = jdbc.update(
            "DELETE FROM subscription WHERE chat_id = ? AND link_id = ?",
            chatId, linkId
        );

        if (rows > 0) {
            // пока нет тегов/фильтров — возвращаем пустые множества
            return Optional.of(new LinkInfo(linkId, url));
        }
        return Optional.empty();
    }

    @Override
    public Page<LinkInfo> findLinksForCheck(Instant threshold, Pageable pageable) {
        int offset = (int) pageable.getOffset();
        int limit  = pageable.getPageSize();

        List<LinkInfo> links = jdbc.query(
            """
            SELECT id, url FROM link
              WHERE last_checked_at < ?
              ORDER BY last_checked_at
              LIMIT ? OFFSET ?
            """,
            (rs, rowNum) -> new LinkInfo(
                rs.getLong("id"),
                rs.getString("url")
            ),
            Timestamp.from(threshold), limit, offset
        );

        Integer total = jdbc.queryForObject(
            "SELECT COUNT(*) FROM link WHERE last_checked_at < ?",
            Integer.class,
            Timestamp.from(threshold)
        );

        return new PageImpl<>(links, pageable, total == null ? 0 : total);
    }

    @Override
    public void updateCheckTime(long linkId, Instant checkedAt, Instant updatedAt) {
        jdbc.update(
            "UPDATE link SET last_checked_at = ?, last_updated_at = ? WHERE id = ?",
            Timestamp.from(checkedAt),
            (updatedAt == null ? null : Timestamp.from(updatedAt)),
            linkId
        );
    }

    @Override
    public List<LinkInfo> findAllByChat(long chatId) {
        return jdbc.query(
            """
            SELECT l.id, l.url
              FROM link l
              JOIN subscription s ON l.id = s.link_id
             WHERE s.chat_id = ?
            """,
            (rs, rowNum) -> new LinkInfo(
                rs.getLong("id"),
                rs.getString("url")
            ),
            chatId
        );
    }
}
