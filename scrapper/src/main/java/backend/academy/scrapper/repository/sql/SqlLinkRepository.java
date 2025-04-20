package backend.academy.scrapper.repository.sql;

import backend.academy.scrapper.dto.LinkInfo;
import backend.academy.scrapper.repository.ILinkRepository;
import java.sql.ResultSet;
import java.util.*;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "access-type", havingValue = "SQL")
public class SqlLinkRepository implements ILinkRepository {
    private final JdbcTemplate jdbc;

    public SqlLinkRepository(DataSource ds) {
        this.jdbc = new JdbcTemplate(ds);
    }

    @Override
    public List<LinkInfo> getLinks(Long chatId) {
        String sql = """
            SELECT link, tags, filters, last_updated
              FROM links
             WHERE chat_id = ?
        """;
        return jdbc.query(sql, new Object[]{chatId}, (ResultSet rs, int rowNum) -> {
            var link = rs.getString("link");
            var tags = parseSet(rs.getString("tags"));
            var filters = parseSet(rs.getString("filters"));
            LinkInfo info = new LinkInfo(link, tags, filters);
            info.getUpdateInfo().setLastUpdated(rs.getString("last_updated"));
            return info;
        });
    }

    @Override
    public void addLink(Long chatId, LinkInfo li) {
        String sql = """
            INSERT INTO links(chat_id, link, tags, filters, last_updated)
            VALUES (?, ?, ?, ?, '')
            """;
        jdbc.update(sql,
            chatId,
            li.getLink(),
            stringify(li.getTags()),
            stringify(li.getFilters())
        );
    }

    @Override
    public Optional<LinkInfo> removeLink(Long chatId, String link) {
        String sel = "SELECT tags, filters, last_updated FROM links WHERE chat_id = ? AND link = ?";
        var list = jdbc.query(sel, new Object[]{chatId, link}, (rs, i) -> {
            var tags = parseSet(rs.getString("tags"));
            var filters = parseSet(rs.getString("filters"));
            LinkInfo info = new LinkInfo(link, tags, filters);
            info.getUpdateInfo().setLastUpdated(rs.getString("last_updated"));
            return info;
        });
        if (list.isEmpty()) return Optional.empty();
        jdbc.update("DELETE FROM links WHERE chat_id = ? AND link = ?", chatId, link);
        return Optional.of(list.get(0));
    }

    private static Set<String> parseSet(String s) {
        if (s == null || s.isBlank()) return Collections.emptySet();
        return new HashSet<>(Arrays.asList(s.split(",")));
    }
    private static String stringify(Set<String> set) {
        return set == null || set.isEmpty() ? "" : String.join(",", set);
    }
}
