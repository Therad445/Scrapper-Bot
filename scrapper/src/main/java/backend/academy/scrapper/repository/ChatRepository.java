package backend.academy.scrapper.repository;

import org.springframework.stereotype.Repository;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ChatRepository {
    private final Set<Long> chats = ConcurrentHashMap.newKeySet();

    public void register(Long chatId) {
        chats.add(chatId);
    }

    public void delete(Long chatId) {
        chats.remove(chatId);
    }

    public boolean exists(Long chatId) {
        return chats.contains(chatId);
    }
}
