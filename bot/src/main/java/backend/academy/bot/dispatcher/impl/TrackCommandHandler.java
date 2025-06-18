package backend.academy.bot.dispatcher.impl;

import backend.academy.bot.config.BotProperties;
import backend.academy.bot.dispatcher.BotCommand;
import backend.academy.bot.dispatcher.CommandHandler;
import backend.academy.bot.dto.LinkResponse;
import backend.academy.bot.service.LinkService;
import backend.academy.bot.service.MessageSenderService;
import backend.academy.bot.service.SessionService;
import backend.academy.bot.state.BotState;
import backend.academy.bot.state.UserSession;
import com.pengrad.telegrambot.model.Update;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@BotCommand
public class TrackCommandHandler implements CommandHandler {

    private final LinkService linkService;
    private final MessageSenderService sender;
    private final SessionService sessionService;
    private final RedisTemplate<String, List<LinkResponse>> redisTemplate;
    private final BotProperties botProps;
    private static final Pattern trackPattern = Pattern.compile("^/track(\\s+|$).*");

    public TrackCommandHandler(
            LinkService linkService,
            MessageSenderService sender,
            SessionService sessionService,
            RedisTemplate<String, List<LinkResponse>> redisTemplate,
            BotProperties botProps) {
        this.linkService = linkService;
        this.sender = sender;
        this.sessionService = sessionService;
        this.redisTemplate = redisTemplate;
        this.botProps = botProps;
    }

    @Override
    public boolean supports(Update u) {
        return u.message() != null && trackPattern.matcher(u.message().text()).matches();
    }

    @Override
    public void handle(Update u) {

        Long chatId = u.message().chat().id();
        String[] parts = u.message().text().split("\\s+", 2);

        if (parts.length < 2 || parts[1].isBlank()) {
            sender.send(chatId,
                "❗️ Вы забыли указать ссылку.\n" +
                    "Использование: /track <url>");
            return;
        }

        String url = parts[1].trim();

        List<LinkResponse> existing = linkService.list(chatId);
        if (existing.stream().anyMatch(lr -> lr.getLink().toString().equals(url))) {
            sender.send(chatId, "Ссылка уже отслеживается ✅");
            return;
        }

        UserSession session = sessionService.getSession(chatId);
        session.setPendingUrl(url);
        session.setState(BotState.WAITING_FOR_TAGS);
        sessionService.saveSession(chatId, session);

        String redisKey = "bot:list:" + chatId;
        redisTemplate.delete(redisKey);

        sender.send(chatId, "Введите тэги (опционально):");
    }
}
