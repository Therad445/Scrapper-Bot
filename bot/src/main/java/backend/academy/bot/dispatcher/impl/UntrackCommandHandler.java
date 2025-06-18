package backend.academy.bot.dispatcher.impl;

import backend.academy.bot.dispatcher.BotCommand;
import backend.academy.bot.dispatcher.CommandHandler;
import backend.academy.bot.service.LinkService;
import backend.academy.bot.service.MessageSenderService;
import com.pengrad.telegrambot.model.Update;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.regex.Pattern;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@BotCommand
public class UntrackCommandHandler implements CommandHandler {

    private static final Pattern UNTRACK_CMD = Pattern.compile("^/untrack(\\s+|$).*");

    private final LinkService linkService;
    private final MessageSenderService sender;
    private final RedisTemplate<String, ?> redisTemplate;

    public UntrackCommandHandler(
            LinkService linkService, MessageSenderService sender, RedisTemplate<String, ?> redisTemplate) {
        this.linkService = linkService;
        this.sender = sender;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean supports(Update u) {
        return CommandHandler.textMatches(u, UNTRACK_CMD);
    }

    @Override
    public void handle(Update u) {
        long chatId = u.message().chat().id();
        String[] parts = u.message().text().split("\\s+", 2);

        if (parts.length < 2 || parts[1].isBlank()) {
            sender.send(chatId, "Вы забыли указать ссылку.\nИспользование: /untrack <url>");
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

        String url = URI.create(raw).normalize().toString();

        try {
            linkService.untrack(chatId, URI.create(url));
            sender.send(chatId, "Ссылка больше не отслеживается");
            redisTemplate.delete("bot:list:" + chatId);
        } catch (Exception ex) {
            sender.send(chatId, "Не удалось удалить ссылку: " + ex.getMessage());
        }
    }
}
