package backend.academy.bot.dispatcher.impl;

import backend.academy.bot.config.BotProperties;
import backend.academy.bot.dispatcher.BotCommand;
import backend.academy.bot.dispatcher.CommandHandler;
import backend.academy.bot.service.LinkService;
import backend.academy.bot.service.MessageSenderService;
import com.pengrad.telegrambot.model.Update;
import java.net.URI;
import java.util.regex.Pattern;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@BotCommand
public class UntrackCommandHandler implements CommandHandler {

    private final LinkService linkService;
    private final MessageSenderService sender;
    private final RedisTemplate<String, ?> redisTemplate;
    private final BotProperties botProps;
    private static final Pattern untrackPattern = Pattern.compile("^/untrack(\\s+|$).*");

    public UntrackCommandHandler(
            LinkService linkService,
            MessageSenderService sender,
            RedisTemplate<String, ?> redisTemplate,
            BotProperties botProps) {
        this.linkService = linkService;
        this.sender = sender;
        this.redisTemplate = redisTemplate;
        this.botProps = botProps;
    }

    @Override
    public boolean supports(Update u) {
        return u.message() != null && untrackPattern.matcher(u.message().text()).matches();
    }

    @Override
    public void handle(Update u) {
        Long chatId = u.message().chat().id();
        String[] parts = u.message().text().split("\\s+", 2);

        if (parts.length < 2 || parts[1].isBlank()) {
            sender.send(chatId,
                "❗️ Вы забыли указать ссылку.\n" +
                    "Использование: /untrack <url>");
            return;
        }

        String url = parts[1].trim();

        try {
            linkService.untrack(chatId, URI.create(url));
            sender.send(chatId, "Ссылка больше не отслеживается");

            String redisKey = "bot:list:" + chatId;
            redisTemplate.delete(redisKey);
        } catch (Exception ex) {
            sender.send(chatId, "Не удалось удалить ссылку: " + ex.getMessage());
        }
    }
}
