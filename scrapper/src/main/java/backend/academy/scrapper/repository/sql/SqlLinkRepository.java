package backend.academy.scrapper.repository.sql;

import backend.academy.scrapper.model.LinkInfo;
import backend.academy.scrapper.repository.LinkRepository;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "SQL")
public class SqlLinkRepository implements LinkRepository {

    private final JdbcTemplate jdbc;

    public SqlLinkRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private Long resolveTagId(String name) {
        return jdbc.queryForObject("""
            INSERT INTO tag(name)
            VALUES (?)
            ON CONFLICT(name) DO UPDATE SET name = EXCLUDED.name
            RETURNING id
            """, Long.class, name);
    }

    private void upsertSubscriptionTag(long chat, long link, long tag) {
        jdbc.update("""
            INSERT INTO subscription_tag(tag_id, chat_id, link_id)
            VALUES (?,?,?) ON CONFLICT DO NOTHING
            """, tag, chat, link);
    }

    private Long resolveFilterId(String name) {
        return jdbc.queryForObject("""
            INSERT INTO filter(name)
            VALUES (?)
            ON CONFLICT(name) DO UPDATE SET name = EXCLUDED.name
            RETURNING id
            """, Long.class, name);
    }

    private void upsertSubscriptionFilter(long chat, long link, long filter) {
        jdbc.update("""
            INSERT INTO subscription_filter(filter_id, chat_id, link_id)
            VALUES (?,?,?) ON CONFLICT DO NOTHING
            """, filter, chat, link);
    }

    @Override
    public void add(long chatId, String url,
                    Set<String> tags, Set<String> filters) {

        Long linkId = getOrCreateLink(url);

        jdbc.update("""
            INSERT INTO subscription(chat_id, link_id)
            VALUES (?, ?) ON CONFLICT DO NOTHING
            """, chatId, linkId);

        tags.forEach(t -> upsertSubscriptionTag(
            chatId, linkId, resolveTagId(t)));

        filters.forEach(f -> upsertSubscriptionFilter(
            chatId, linkId, resolveFilterId(f)));
    }

    @Override
    public void addTag(long chat, long link, String tag) {
        upsertSubscriptionTag(chat, link, resolveTagId(tag));
    }

    @Override
    public void removeTag(long chat, long link, String tag) {
        jdbc.update("""
            DELETE FROM subscription_tag
            WHERE chat_id=? AND link_id=? AND tag_id=(
                 SELECT id FROM tag WHERE name=?)
            """, chat, link, tag);
    }

    @Override
    public void addFilter(long chat, long link, String filter) {
        upsertSubscriptionFilter(chat, link, resolveFilterId(filter));
    }

    @Override
    public void removeFilter(long chat, long link, String filter) {
        jdbc.update("""
            DELETE FROM subscription_filter
            WHERE chat_id=? AND link_id=? AND filter_id=(
                 SELECT id FROM filter WHERE name=?)
            """, chat, link, filter);
    }

    @Override
    public Optional<LinkInfo> remove(long chatId, String url) {
        Long linkId = jdbc.query("""
            SELECT id FROM link WHERE url = ?
            """, rs -> rs.next() ? rs.getLong(1) : null, url);

        if (linkId == null) return Optional.empty();

        int rows = jdbc.update("""
            DELETE FROM subscription
            WHERE chat_id=? AND link_id=?""", chatId, linkId);

        return rows > 0 ? Optional.of(
            new LinkInfo(linkId, url, null, null, Set.of(), Set.of()))
            : Optional.empty();
    }

    @Override
    public Page<LinkInfo> findLinksForCheck(Instant th, Pageable p) {
        List<LinkInfo> list = jdbc.query("""
                SELECT id, url, last_checked_at, last_updated_at
                  FROM link
                 WHERE last_checked_at < ?
                 ORDER BY last_checked_at
                 LIMIT ? OFFSET ?""",
            (rs, i) -> new LinkInfo(
                rs.getLong("id"),
                rs.getString("url"),
                rs.getTimestamp("last_checked_at").toInstant(),
                rs.getTimestamp("last_updated_at") == null
                    ? null : rs.getTimestamp("last_updated_at").toInstant(),
                Set.of(), Set.of()),
            Timestamp.from(th), p.getPageSize(), p.getOffset() // <--- добавлено
        );

        Integer total = jdbc.queryForObject("""
                SELECT count(*) FROM link WHERE last_checked_at < ?""",
            Integer.class, Timestamp.from(th));

        return new PageImpl<>(list, p, total == null ? 0 : total);
    }


    @Override
    public void updateCheckTime(long linkId, Instant check, Instant upd) {
        jdbc.update("""
                UPDATE link
                SET last_checked_at=?, last_updated_at=?
                WHERE id = ?""",
            Timestamp.from(check),
            upd == null ? null : Timestamp.from(upd),
            linkId);
    }

    @Override
    public List<LinkInfo> findAllByChat(long chatId) {
        return jdbc.query("""
                SELECT l.id, l.url, l.last_checked_at, l.last_updated_at
                  FROM link l
                  JOIN subscription s ON s.link_id = l.id
                 WHERE s.chat_id=?""",
            (rs, i) -> new LinkInfo(
                rs.getLong("id"),
                rs.getString("url"),
                rs.getTimestamp("last_checked_at").toInstant(),
                rs.getTimestamp("last_updated_at") == null ? null
                    : rs.getTimestamp("last_updated_at").toInstant(),
                Set.of(), Set.of()),
            chatId
        );
    }

    private Long getOrCreateLink(String url) {
        try {
            return jdbc.queryForObject(
                "SELECT id FROM link WHERE url=?", Long.class, url);
        } catch (EmptyResultDataAccessException e) {
            return jdbc.queryForObject("""
                    INSERT INTO link(url,last_checked_at)
                    VALUES (?,?) RETURNING id""",
                Long.class, url, Timestamp.from(Instant.EPOCH));
        }
    }
}
