package backend.academy.scrapper.repository.orm;

import backend.academy.scrapper.entity.ChatEntity;
import backend.academy.scrapper.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "access-type", havingValue = "ORM")
public class OrmChatRepository implements ChatRepository {

    private final ChatJpaRepository jpa;

    @Override
    public void register(long chatId) {
        if (!jpa.existsById(chatId)) {
            jpa.save(new ChatEntity(chatId, Instant.now()));
        }
    }

    @Override
    public void delete(long chatId) {
        jpa.deleteById(chatId);
    }

    @Override
    public boolean exists(long chatId) {
        return jpa.existsById(chatId);
    }

    @Override
    public List<Long> findChatIdsByLinkId(long linkId) {
        return jpa.findChatIdsByLinkId(linkId);
    }
}

