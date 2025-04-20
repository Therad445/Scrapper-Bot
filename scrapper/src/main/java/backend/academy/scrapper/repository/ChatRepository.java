package backend.academy.scrapper.repository;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

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

    public Set<Long> getAllChats() {
        return Collections.unmodifiableSet(chats);
    }
}
