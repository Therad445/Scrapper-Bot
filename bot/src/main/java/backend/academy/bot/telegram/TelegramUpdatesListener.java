package backend.academy.bot.telegram;

import backend.academy.bot.dispatcher.CommandDispatcher;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TelegramUpdatesListener {

    private final TelegramBot bot;
    private final CommandDispatcher dispatcher;

    @PostConstruct
    void init() {
        bot.setUpdatesListener(updates -> {
            updates.forEach(dispatcher::dispatch);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}
