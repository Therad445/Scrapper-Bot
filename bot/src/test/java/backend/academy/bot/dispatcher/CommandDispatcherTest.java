package backend.academy.bot.dispatcher;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.bot.dispatcher.impl.UnknownCommandHandler;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CommandDispatcherTest {

    CommandHandler handler1;
    CommandHandler handler2;
    UnknownCommandHandler unknown;
    CommandDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        handler1 = mock(CommandHandler.class);
        handler2 = mock(CommandHandler.class);
        unknown = mock(UnknownCommandHandler.class);
        dispatcher = new CommandDispatcher(List.of(handler1, handler2), unknown);
    }

    @Test
    void shouldDispatchToFirstSupportingHandler() {
        Update update = buildUpdate("/start");

        when(handler1.supports(update)).thenReturn(false);
        when(handler2.supports(update)).thenReturn(true);

        dispatcher.dispatch(update);

        verify(handler2).handle(update);
        verify(handler1, never()).handle(update);
    }

    @Test
    void shouldThrowIfNoHandlerSupportsUpdate() {
        Update update = buildUpdate("/unknown");

        when(handler1.supports(update)).thenReturn(false);
        when(handler2.supports(update)).thenReturn(false);

        dispatcher.dispatch(update);

        verify(unknown).handle(update);
        verify(handler1, never()).handle(update);
        verify(handler2, never()).handle(update);
    }

    private Update buildUpdate(String text) {
        Chat chat = mock(Chat.class);
        when(chat.id()).thenReturn(123L);

        Message message = mock(Message.class);
        when(message.text()).thenReturn(text);
        when(message.chat()).thenReturn(chat);

        Update update = mock(Update.class);
        when(update.message()).thenReturn(message);

        return update;
    }
}
