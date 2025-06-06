package backend.academy.bot.dispatcher.impl;

import backend.academy.bot.config.BotProperties;
import backend.academy.bot.dispatcher.BotCommand;
import backend.academy.bot.dispatcher.CommandHandler;
import backend.academy.bot.dto.LinkResponse;
import backend.academy.bot.service.LinkService;
import backend.academy.bot.service.MessageSenderService;
import com.pengrad.telegrambot.model.Update;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@BotCommand
public class ListCommandHandler implements CommandHandler {

    private final LinkService linkService;
    private final MessageSenderService sender;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final BotProperties botProps;
    private final RedisTemplate<String, List<LinkResponse>> redisTemplate;

    public ListCommandHandler(
            LinkService linkService,
            MessageSenderService sender,
            KafkaTemplate<String, Object> kafkaTemplate,
            BotProperties botProps,
            RedisTemplate<String, List<LinkResponse>> redisTemplate) {
        this.linkService = linkService;
        this.sender = sender;
        this.kafkaTemplate = kafkaTemplate;
        this.botProps = botProps;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean supports(Update u) {
        return u.message() != null && "/list".equals(u.message().text().trim());
    }

    @Override
    public void handle(Update u) {
        Long chatId = u.message().chat().id();
        String redisKey = "bot:list:" + chatId;

        List<LinkResponse> cached = redisTemplate.opsForValue().get(redisKey);
        if (cached != null) {
            if (cached.isEmpty()) {
                sender.send(chatId, "Вы ничего не отслеживаете 🤷‍♂️");
            } else {
                String body = cached.stream().map(lr -> lr.getLink().toString()).collect(Collectors.joining("\n"));
                sender.send(chatId, "Ваши подписки:\n" + body + " (из кеша)");
            }
            return;
        }

        var freshList = linkService.list(chatId);

        redisTemplate
                .opsForValue()
                .set(redisKey, freshList, botProps.getCacheTtl().getSeconds(), TimeUnit.SECONDS);

        if (freshList.isEmpty()) {
            sender.send(chatId, "Вы ничего не отслеживаете 🤷‍♂️");
        } else {
            String body = freshList.stream().map(lr -> lr.getLink().toString()).collect(Collectors.joining("\n"));
            sender.send(chatId, "Ваши подписки:\n" + body);
        }
    }
}
