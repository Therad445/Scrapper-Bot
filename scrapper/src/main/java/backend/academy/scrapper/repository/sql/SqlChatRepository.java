package backend.academy.scrapper.repository.sql;

import backend.academy.scrapper.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@ConditionalOnProperty(name="access-type", havingValue="SQL")
@RequiredArgsConstructor
public class SqlChatRepository implements ChatRepository {
    private final JdbcTemplate jdbc;
    @Override public void register(long id){
        jdbc.update("insert into chat(id,created_at) values (?,now()) on conflict do nothing", id);
    }
    @Override public void delete(long id){
        jdbc.update("delete from chat where id=?", id);
    }
    @Override public boolean exists(long id){
        return Boolean.TRUE.equals(
            jdbc.queryForObject("select exists(select 1 from chat where id=?)", Boolean.class, id));
    }
    @Override public List<Long> findChatIdsByLinkId(long linkId){
        return jdbc.queryForList("select chat_id from subscription where link_id=?", Long.class, linkId);
    }
}
