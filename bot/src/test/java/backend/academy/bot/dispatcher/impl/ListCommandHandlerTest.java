package backend.academy.bot.dispatcher.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.bot.dto.LinkResponse;
import backend.academy.bot.service.LinkService;
import backend.academy.bot.service.MessageSenderService;
import backend.academy.bot.config.BotProperties;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.core.KafkaTemplate;

class ListCommandHandlerTest {

    private LinkService linkService;
    private MessageSenderService sender;
    private KafkaTemplate<String, Object> kafkaTemplate;
    private BotProperties botProps;
    @SuppressWarnings("unchecked")
    private RedisTemplate<String, List<LinkResponse>> redisTemplate;
    @SuppressWarnings("unchecked")
    private ValueOperations<String, List<LinkResponse>> valueOps;
    private ListCommandHandler handler;

    @BeforeEach
    void setUp() {
        linkService = mock(LinkService.class);
        sender = mock(MessageSenderService.class);
        kafkaTemplate = mock(KafkaTemplate.class);
        botProps = mock(BotProperties.class);
        redisTemplate = mock(RedisTemplate.class);
        valueOps = mock(ValueOperations.class);

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(botProps.getCacheTtl()).thenReturn(Duration.ofSeconds(60));

        handler = new ListCommandHandler(
            linkService,
            sender,
            kafkaTemplate,
            botProps,
            redisTemplate
        );
    }

    private Update createUpdate(Long chatId, String text) {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn(text);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(chatId);

        return update;
    }

    @Test
    void supports_ShouldReturnTrue_ForListCommand() {
        Update update = createUpdate(123L, "/list");
        boolean result = handler.supports(update);
        assert result;
    }

    @Test
    void supports_ShouldReturnFalse_ForOtherCommand() {
        Update update = createUpdate(123L, "/help");
        boolean result = handler.supports(update);
        assert !result;
    }

    @Test
    void handle_ShouldSendEmptyMessage_WhenNoLinks_CacheHit() {
        Long chatId = 123L;
        Update update = createUpdate(chatId, "/list");
        String redisKey = "bot:list:" + chatId;

        when(valueOps.get(redisKey)).thenReturn(Collections.emptyList());

        handler.handle(update);

        verify(sender).send(chatId, "Вы ничего не отслеживаете 🤷‍♂️");
    }

    @Test
    void handle_ShouldSendCachedListMessage_WhenLinksExist_CacheHit() {
        Long chatId = 456L;
        Update update = createUpdate(chatId, "/list");
        String redisKey = "bot:list:" + chatId;

        List<LinkResponse> cachedLinks = List.of(
            new LinkResponse(1L, URI.create("https://example.com/1"), List.of(), List.of()),
            new LinkResponse(2L, URI.create("https://example.com/2"), List.of(), List.of())
        );
        when(valueOps.get(redisKey)).thenReturn(cachedLinks);

        handler.handle(update);

        verify(sender).send(
            chatId,
            "Ваши подписки:\nhttps://example.com/1\nhttps://example.com/2 (из кеша)"
        );
    }

    @Test
    void handle_ShouldSendEmptyMessage_WhenNoLinks_CacheMiss() {
        Long chatId = 789L;
        Update update = createUpdate(chatId, "/list");
        String redisKey = "bot:list:" + chatId;

        when(valueOps.get(redisKey)).thenReturn(null);
        when(linkService.list(chatId)).thenReturn(Collections.emptyList());

        handler.handle(update);

        verify(valueOps).set(redisKey, Collections.emptyList(), 60L, TimeUnit.SECONDS);
        verify(sender).send(chatId, "Вы ничего не отслеживаете 🤷‍♂️");
    }

    @Test
    void handle_ShouldSendListMessage_WhenLinksExist_CacheMiss() {
        Long chatId = 321L;
        Update update = createUpdate(chatId, "/list");
        String redisKey = "bot:list:" + chatId;

        when(valueOps.get(redisKey)).thenReturn(null);

        List<LinkResponse> freshLinks = List.of(
            new LinkResponse(3L, URI.create("https://example.com/3"), List.of(), List.of()),
            new LinkResponse(4L, URI.create("https://example.com/4"), List.of(), List.of())
        );
        when(linkService.list(chatId)).thenReturn(freshLinks);

        handler.handle(update);

        verify(valueOps).set(redisKey, freshLinks, 60L, TimeUnit.SECONDS);
        verify(sender).send(
            chatId,
            "Ваши подписки:\nhttps://example.com/3\nhttps://example.com/4"
        );
    }
}
