package backend.academy.scrapper.repository.sql;

import backend.academy.scrapper.repository.ChatRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "SQL")
@RequiredArgsConstructor
public class SqlChatRepository implements ChatRepository {

    private final JdbcTemplate jdbc;

    @Override
    public void register(long id) {
        jdbc.update("""
            INSERT INTO chat(id, created_at)
            VALUES (?, now())
            ON CONFLICT DO NOTHING
            """, id);
    }

    @Override
    public void delete(long id) {
        jdbc.update("""
            DELETE FROM chat
            WHERE id = ?
            """, id);
    }

    @Override
    public boolean exists(long id) {
        return Boolean.TRUE.equals(jdbc.queryForObject("""
            SELECT EXISTS (
                SELECT 1 FROM chat WHERE id = ?
            )
            """, Boolean.class, id));
    }

    @Override
    public List<Long> findChatIdsByLinkId(long linkId) {
        return jdbc.queryForList("""
            SELECT chat_id
              FROM subscription
             WHERE link_id = ?
            """, Long.class, linkId);
    }
}
