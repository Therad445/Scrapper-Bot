package backend.academy.bot.dispatcher.impl;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.bot.service.LinkService;
import backend.academy.bot.service.MessageSenderService;
import backend.academy.bot.service.SessionService;
import backend.academy.bot.state.BotState;
import backend.academy.bot.state.UserSession;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SessionInputHandlerTest {

    private final Long chatId = 123L;
    private final String pendingUrl = "https://example.com";
    private SessionService sessionService;
    private LinkService linkService;
    private MessageSenderService sender;
    private SessionInputHandler handler;

    @BeforeEach
    void setUp() {
        sessionService = mock(SessionService.class);
        linkService = mock(LinkService.class);
        sender = mock(MessageSenderService.class);
        handler = new SessionInputHandler(sessionService, linkService, sender);
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
    void supports_ShouldReturnTrue_IfStateIsNotNone() {
        UserSession session = mock(UserSession.class);
        when(session.getState()).thenReturn(BotState.WAITING_FOR_TAGS);
        when(sessionService.getSession(chatId)).thenReturn(session);

        Update update = mockUpdate("some text");
        assert handler.supports(update);
    }

    @Test
    void supports_ShouldReturnFalse_IfStateIsNone() {
        UserSession session = mock(UserSession.class);
        when(session.getState()).thenReturn(BotState.NONE);
        when(sessionService.getSession(chatId)).thenReturn(session);

        Update update = mockUpdate("any");
        assert !handler.supports(update);
    }

    @Test
    void handle_ShouldSetTags_SaveSession_AndAskForFilters_WhenWaitingForTags() {
        UserSession session = mock(UserSession.class);
        when(session.getState()).thenReturn(BotState.WAITING_FOR_TAGS);
        when(sessionService.getSession(chatId)).thenReturn(session);

        Update update = mockUpdate("tag1 tag2");

        handler.handle(update);

        verify(session).setPendingTags("tag1 tag2");
        verify(session).setState(BotState.WAITING_FOR_FILTERS);
        verify(sessionService).saveSession(chatId, session);
        verify(sender).send(chatId, "Введите фильтры (опционально):");
    }

    @Test
    void handle_ShouldTrackLink_DeleteSession_WhenWaitingForFilters() {
        UserSession session = mock(UserSession.class);
        when(session.getState()).thenReturn(BotState.WAITING_FOR_FILTERS);
        when(session.getPendingTags()).thenReturn("tag1 tag2");
        when(session.getPendingUrl()).thenReturn(pendingUrl);
        when(session.getPendingFilters()).thenReturn("filter1 filter2");
        when(sessionService.getSession(chatId)).thenReturn(session);

        Update update = mockUpdate("filter1 filter2");

        handler.handle(update);

        verify(session).setPendingFilters("filter1 filter2");
        verify(linkService).track(
            eq(chatId),
            eq(URI.create(pendingUrl)),
            eq(List.of("tag1", "tag2")),
            eq(List.of("filter1", "filter2"))
        );
        verify(sender).send(chatId, "Ссылка добавлена с тэгами и фильтрами ✅");
        verify(session).reset();
        verify(sessionService).deleteSession(chatId);
    }

    @Test
    void handle_ShouldHandleBlankTags_AsNull_SaveSession() {
        UserSession session = mock(UserSession.class);
        when(session.getState()).thenReturn(BotState.WAITING_FOR_TAGS);
        when(sessionService.getSession(chatId)).thenReturn(session);

        Update update = mockUpdate("   ");

        handler.handle(update);

        verify(session).setPendingTags(null);
        verify(session).setState(BotState.WAITING_FOR_FILTERS);
        verify(sessionService).saveSession(chatId, session);
        verify(sender).send(chatId, "Введите фильтры (опционально):");
    }

    @Test
    void handle_ShouldHandleBlankFilters_AsEmptyList_AndDeleteSession() {
        UserSession session = mock(UserSession.class);
        when(session.getState()).thenReturn(BotState.WAITING_FOR_FILTERS);
        when(session.getPendingTags()).thenReturn(null);
        when(session.getPendingUrl()).thenReturn(pendingUrl);
        when(sessionService.getSession(chatId)).thenReturn(session);

        Update update = mockUpdate("   ");

        handler.handle(update);

        verify(session).setPendingFilters(null);
        verify(linkService).track(
            eq(chatId),
            eq(URI.create(pendingUrl)),
            eq(List.of()),
            eq(List.of())
        );
        verify(sender).send(chatId, "Ссылка добавлена с тэгами и фильтрами ✅");
        verify(session).reset();
        verify(sessionService).deleteSession(chatId);
    }
}
