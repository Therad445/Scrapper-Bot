package backend.academy.bot.dispatcher.impl;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.bot.service.LinkService;
import backend.academy.bot.service.MessageSenderService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UntrackCommandHandlerTest {

    private final Long chatId = 123L;
    private final String url = "https://example.com";
    private LinkService linkService;
    private MessageSenderService sender;
    private UntrackCommandHandler handler;

    @BeforeEach
    void setUp() {
        linkService = mock(LinkService.class);
        sender = mock(MessageSenderService.class);
        handler = new UntrackCommandHandler(linkService, sender);
    }

    private Update mockUpdate(String text) {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(chatId);
        when(message.text()).thenReturn(text);

        return update;
    }

    @Test
    void supports_ShouldReturnTrue_WhenTextStartsWithUntrack() {
        Update update = mockUpdate("/untrack " + url);
        assert handler.supports(update);
    }

    @Test
    void handle_ShouldCallUntrackAndSendSuccessMessage() {
        Update update = mockUpdate("/untrack " + url);

        handler.handle(update);

        verify(linkService).untrack(chatId, URI.create(url));
        verify(sender).send(chatId, "Ссылка больше не отслеживается");
    }

    @Test
    void handle_ShouldSendErrorMessage_WhenUntrackThrowsException() {
        Update update = mockUpdate("/untrack " + url);
        doThrow(new RuntimeException("Ошибка")).when(linkService).untrack(any(), any());

        handler.handle(update);

        verify(sender).send(eq(chatId), contains("Не удалось удалить ссылку: Ошибка"));
    }
}
