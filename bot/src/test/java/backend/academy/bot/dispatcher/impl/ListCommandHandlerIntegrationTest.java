package backend.academy.bot.dispatcher.impl;

import backend.academy.bot.BotApplication;
import backend.academy.bot.dto.LinkResponse;
import backend.academy.bot.service.LinkService;
import backend.academy.bot.service.MessageSenderService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(
    classes = BotApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {
        "app.scrapper-url=http://localhost",
        "app.telegram-token=dummy-token",
        "app.cache-ttl=1s"
    }
)
@EnableAutoConfiguration(
    exclude = {
        KafkaAutoConfiguration.class
    }
)
@ActiveProfiles("test")
@Testcontainers
@Import(TestcontainersConfiguration.class)
class ListCommandHandlerIntegrationTest {

    private static final Long CHAT_ID = 777L;
    private static final String REDIS_KEY = "bot:list:" + CHAT_ID;

    @Autowired
    private ListCommandHandler listHandler;

    @Autowired
    private TrackCommandHandler trackHandler;

    @Autowired
    private RedisTemplate<String, List<LinkResponse>> redisTemplate;

    @Mock
    private LinkService linkService;

    @Mock
    private MessageSenderService sender;

    @BeforeEach
    void setUp() {
        redisTemplate.delete(REDIS_KEY);
        reset(linkService, sender);
    }

    @Test
    @Timeout(value = 20, unit = TimeUnit.SECONDS)
    void firstListRequest_shouldLoadFromService_andThenCacheReplay_untilInvalidation() {
        LinkResponse lr1 = new LinkResponse(1L, URI.create("https://a.com"), Collections.emptyList(), Collections.emptyList());
        LinkResponse lr2 = new LinkResponse(2L, URI.create("https://b.com"), Collections.emptyList(), Collections.emptyList());
        List<LinkResponse> freshList = List.of(lr1, lr2);
        when(linkService.list(CHAT_ID)).thenReturn(freshList);

        Update listUpdate = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(listUpdate.message()).thenReturn(message);
        when(message.text()).thenReturn("/list");
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(CHAT_ID);

        listHandler.handle(listUpdate);
        List<LinkResponse> cached = redisTemplate.opsForValue().get(REDIS_KEY);
        assertThat(cached).isEqualTo(freshList);

        String expectedBody = "Ваши подписки:\nhttps://a.com\nhttps://b.com";
        verify(sender, times(1)).send(CHAT_ID, expectedBody);

        reset(sender, linkService);
        listHandler.handle(listUpdate);

        String expectedCachedBody = "Ваши подписки:\nhttps://a.com\nhttps://b.com (из кеша)";
        verify(sender, times(1)).send(CHAT_ID, expectedCachedBody);
        verifyNoInteractions(linkService);

        Update trackUpdate = mock(Update.class);
        Message msgTrack = mock(Message.class);
        Chat chatTrack = mock(Chat.class);
        when(trackUpdate.message()).thenReturn(msgTrack);
        when(msgTrack.text()).thenReturn("/track https://new.example");
        when(msgTrack.chat()).thenReturn(chatTrack);
        when(chatTrack.id()).thenReturn(CHAT_ID);
        when(linkService.list(CHAT_ID)).thenReturn(Collections.emptyList());

        trackHandler.handle(trackUpdate);

        assertThat(redisTemplate.hasKey(REDIS_KEY)).isFalse();
    }
}
