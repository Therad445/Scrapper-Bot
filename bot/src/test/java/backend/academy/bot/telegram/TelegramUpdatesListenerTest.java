package backend.academy.bot.telegram;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import backend.academy.bot.dispatcher.CommandDispatcher;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class TelegramUpdatesListenerTest {

    private TelegramBot bot;
    private CommandDispatcher dispatcher;
    private TelegramUpdatesListener listener;

    @BeforeEach
    void setUp() {
        bot = mock(TelegramBot.class);
        dispatcher = mock(CommandDispatcher.class);
        listener = new TelegramUpdatesListener(bot, dispatcher);
    }

    @Test
    void init_ShouldRegisterUpdatesListenerAndDispatchUpdates() {
        ArgumentCaptor<UpdatesListener> captor = ArgumentCaptor.forClass(UpdatesListener.class);

        listener.init();

        verify(bot).setUpdatesListener(captor.capture());

        Update update1 = mock(Update.class);
        Update update2 = mock(Update.class);
        int result = captor.getValue().process(List.of(update1, update2));

        verify(dispatcher).dispatch(update1);
        verify(dispatcher).dispatch(update2);

        assertEquals(UpdatesListener.CONFIRMED_UPDATES_ALL, result);
    }
}
