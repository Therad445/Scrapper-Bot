package backend.academy.bot.dispatcher.impl;

import backend.academy.bot.dispatcher.BotCommand;
import backend.academy.bot.dispatcher.CommandHandler;
import backend.academy.bot.dto.LinkResponse;
import backend.academy.bot.service.LinkService;
import backend.academy.bot.service.MessageSenderService;
import backend.academy.bot.service.SessionService;
import backend.academy.bot.state.BotState;
import backend.academy.bot.state.UserSession;
import com.pengrad.telegrambot.model.Update;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@BotCommand
public class TrackCommandHandler implements CommandHandler {

    private static final Pattern TRACK_CMD = Pattern.compile("^/track(\\s+|$).*");
    private final LinkService linkService;
    private final MessageSenderService sender;
    private final SessionService sessionService;
    private final RedisTemplate<String, List<LinkResponse>> redisTemplate;

    public TrackCommandHandler(
            LinkService linkService,
            MessageSenderService sender,
            SessionService sessionService,
            RedisTemplate<String, List<LinkResponse>> redisTemplate) {
        this.linkService = linkService;
        this.sender = sender;
        this.sessionService = sessionService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean supports(Update u) {
        return CommandHandler.textMatches(u, TRACK_CMD);
    }

    @Override
    public void handle(Update u) {
        long chatId = u.message().chat().id();
        String[] parts = u.message().text().split("\\s+", 2);

        if (parts.length < 2 || parts[1].isBlank()) {
            sender.send(chatId, "Вы забыли указать ссылку.\nИспользование: /track <url>");
            return;
        }

        String raw = parts[1].trim();

        try {
            URL test = new URL(raw);
            if (!"http".equalsIgnoreCase(test.getProtocol()) && !"https".equalsIgnoreCase(test.getProtocol())) {
                throw new MalformedURLException();
            }
        } catch (MalformedURLException ex) {
            sender.send(chatId, "Некорректный URL. Убедитесь, что он начинается с http:// или https://");
            return;
        }

        String url = raw;
        try {
            url = URI.create(parts[1].trim()).normalize().toString();
        } catch (IllegalArgumentException ex) {
            sender.send(chatId, "Некорректный URL");
            return;
        }

        String finalUrl = url;
        if (linkService.list(chatId).stream()
                .anyMatch(lr -> lr.getLink().toString().equals(finalUrl))) {
            sender.send(chatId, "Ссылка уже отслеживается");
            return;
        }

        UserSession session = sessionService.getSession(chatId);
        session.setPendingUrl(url);
        session.setState(BotState.WAITING_FOR_TAGS);
        sessionService.saveSession(chatId, session);

        redisTemplate.delete("bot:list:" + chatId);
        sender.send(chatId, "Введите тэги (опционально):");
    }
}
