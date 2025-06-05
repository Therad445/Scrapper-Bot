package backend.academy.bot.dispatcher.impl;

import backend.academy.bot.dispatcher.BotCommand;
import backend.academy.bot.dispatcher.CommandHandler;
import backend.academy.bot.service.LinkService;
import backend.academy.bot.service.MessageSenderService;
import backend.academy.bot.config.BotProperties;
import com.pengrad.telegrambot.model.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
@BotCommand
public class UntrackCommandHandler implements CommandHandler {

    private final LinkService linkService;
    private final MessageSenderService sender;
    private final RedisTemplate<String, ?> redisTemplate;
    private final BotProperties botProps;

    public UntrackCommandHandler(
        LinkService linkService,
        MessageSenderService sender,
        RedisTemplate<String, ?> redisTemplate,
        BotProperties botProps
    ) {
        this.linkService = linkService;
        this.sender = sender;
        this.redisTemplate = redisTemplate;
        this.botProps = botProps;
    }

    @Override
    public boolean supports(Update u) {
        return u.message() != null && u.message().text().startsWith("/untrack ");
    }

    @Override
    public void handle(Update u) {
        Long chatId = u.message().chat().id();
        String url = u.message().text().substring(9).trim();

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
