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

class HelpCommandHandlerTest {

    private MessageSenderService sender;
    private HelpCommandHandler handler;

    @BeforeEach
    void setUp() {
        sender = mock(MessageSenderService.class);
        handler = new HelpCommandHandler(sender);
    }

    @Test
    void shouldSupportHelpCommand() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("/help");

        boolean result = handler.supports(update);

        assert result;
    }

    @Test
    void shouldNotSupportOtherCommand() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("/start");

        boolean result = handler.supports(update);

        assert !result;
    }

    @Test
    void shouldSendHelpMessage() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123L);
        when(message.text()).thenReturn("/help");

        handler.handle(update);

        verify(sender).send(eq(123L), contains("Доступные команды:"));
    }
}
