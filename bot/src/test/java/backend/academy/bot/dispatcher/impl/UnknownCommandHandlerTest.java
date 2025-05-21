package backend.academy.bot.dispatcher.impl;

import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.bot.service.MessageSenderService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UnknownCommandHandlerTest {

    private final Long chatId = 123L;
    private MessageSenderService sender;
    private UnknownCommandHandler handler;

    @BeforeEach
    void setUp() {
        sender = mock(MessageSenderService.class);
        handler = new UnknownCommandHandler(sender);
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
    void supports_ShouldReturnTrue_WhenMessageExists() {
        Update update = mockUpdate("some text");
        assert handler.supports(update);
    }

    @Test
    void handle_ShouldSendUnknownCommandMessage() {
        Update update = mockUpdate("random text");

        handler.handle(update);

        verify(sender).send(eq(chatId), contains("Неизвестная команда"));
    }
}
