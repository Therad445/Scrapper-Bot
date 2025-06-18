package backend.academy.bot.dispatcher;

import com.pengrad.telegrambot.model.Update;
import java.util.OptionalLong;
import java.util.regex.Pattern;

public interface CommandHandler {
    boolean supports(Update update);

    void handle(Update update);

    static OptionalLong chatId(Update update) {
        return update != null && update.message() != null
            ? OptionalLong.of(update.message().chat().id())
            : OptionalLong.empty();
    }
    static boolean textMatches(Update u, Pattern p) {
        return CommandHandler.chatId(u).isPresent()
            && p.matcher(u.message().text()).matches();
    }
}
