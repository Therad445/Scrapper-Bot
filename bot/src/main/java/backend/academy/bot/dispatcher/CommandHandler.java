package backend.academy.bot.dispatcher;

import com.pengrad.telegrambot.model.Update;
import java.util.OptionalLong;
import java.util.regex.Pattern;

public interface CommandHandler {
    static OptionalLong chatId(Update update) {
        if (update == null) return OptionalLong.empty();
        var msg = update.message();
        if (msg == null || msg.chat() == null) return OptionalLong.empty();
        return OptionalLong.of(msg.chat().id());
    }

    static boolean textMatches(Update u, Pattern p) {
        return u != null
                && u.message() != null
                && u.message().text() != null
                && p.matcher(u.message().text().trim()).matches();
    }

    boolean supports(Update update);

    void handle(Update update);
}
