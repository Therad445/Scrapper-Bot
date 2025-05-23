package backend.academy.bot.dispatcher;

import com.pengrad.telegrambot.model.Update;

public interface CommandHandler {
    boolean supports(Update update);

    void handle(Update update);
}
