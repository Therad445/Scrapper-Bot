package backend.academy.bot.service;

import backend.academy.bot.state.UserSession;
import java.util.Optional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    private final RedisTemplate<String, UserSession> redisTemplate;
    private static final String KEY_PREFIX = "session:";

    public SessionService(RedisTemplate<String, UserSession> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public UserSession getSession(Long chatId) {
        String key = KEY_PREFIX + chatId;
        UserSession session = redisTemplate.opsForValue().get(key);
        if (session == null) {
            session = new UserSession();
            redisTemplate.opsForValue().set(key, session);
        }
        return session;
    }

    public void saveSession(Long chatId, UserSession session) {
        String key = KEY_PREFIX + chatId;
        redisTemplate.opsForValue().set(key, session);
    }

    public void deleteSession(Long chatId) {
        String key = KEY_PREFIX + chatId;
        redisTemplate.delete(key);
    }
}
