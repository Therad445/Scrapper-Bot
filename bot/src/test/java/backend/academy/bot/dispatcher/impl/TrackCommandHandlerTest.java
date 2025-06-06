package backend.academy.bot.dispatcher.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import backend.academy.bot.dto.LinkResponse;
import backend.academy.bot.service.LinkService;
import backend.academy.bot.service.MessageSenderService;
import backend.academy.bot.service.SessionService;
import backend.academy.bot.state.BotState;
import backend.academy.bot.state.UserSession;
import backend.academy.bot.config.BotProperties;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;

class TrackCommandHandlerTest {

    private final Long chatId = 42L;
    private final String url = "https://example.com";
    private LinkService linkService;
    private MessageSenderService sender;
    private SessionService sessionService;
    private RedisTemplate<String, List<LinkResponse>> redisTemplate;
    private BotProperties botProps;
    private TrackCommandHandler handler;

    @BeforeEach
    void setUp() {
        linkService = mock(LinkService.class);
        sender = mock(MessageSenderService.class);
        sessionService = mock(SessionService.class);
        redisTemplate = mock(RedisTemplate.class);
        botProps = mock(BotProperties.class);
        handler = new TrackCommandHandler(
            linkService,
            sender,
            sessionService,
            redisTemplate,
            botProps
        );
    }

    private Update mockUpdate(String text) {
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
    void supports_ShouldReturnTrue_WhenTextStartsWithTrack() {
        Update update = mockUpdate("/track https://example.com");
        assertTrue(handler.supports(update));
    }

    @Test
    void supports_ShouldReturnFalse_WhenTextDoesNotStartWithTrack() {
        Update update = mockUpdate("/list");
        assertFalse(handler.supports(update));
    }

    @Test
    void handle_ShouldSendAlreadyTrackedMessage_IfUrlIsTracked() {
        Update update = mockUpdate("/track " + url);
        LinkResponse existingLink =
            new LinkResponse(chatId, URI.create(url), Collections.emptyList(), Collections.emptyList());

        when(linkService.list(chatId)).thenReturn(List.of(existingLink));

        handler.handle(update);

        verify(sender).send(chatId, "Ссылка уже отслеживается ✅");
        verifyNoInteractions(sessionService);
        verifyNoInteractions(redisTemplate);
    }

    @Test
    void handle_ShouldStartSession_IfUrlNotTracked() {
        Update update = mockUpdate("/track " + url);
        UserSession session = mock(UserSession.class);

        when(linkService.list(chatId)).thenReturn(Collections.emptyList());
        when(sessionService.getSession(chatId)).thenReturn(session);

        handler.handle(update);

        verify(session).setPendingUrl(url);
        verify(session).setState(BotState.WAITING_FOR_TAGS);
        verify(sessionService).saveSession(chatId, session);
        verify(redisTemplate).delete("bot:list:" + chatId);
        verify(sender).send(chatId, "Введите тэги (опционально):");
    }
}
