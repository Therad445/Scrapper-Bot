package backend.academy.bot.service;

import backend.academy.bot.state.UserSession;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
    private final ConcurrentHashMap<Long, UserSession> sessions = new ConcurrentHashMap<>();

    public UserSession getSession(Long chatId) {
        return sessions.computeIfAbsent(chatId, id -> new UserSession());
    }
}
