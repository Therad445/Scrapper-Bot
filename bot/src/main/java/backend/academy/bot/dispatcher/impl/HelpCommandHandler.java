package backend.academy.bot.dispatcher.impl;

import backend.academy.bot.dispatcher.CommandHandler;
import backend.academy.bot.service.MessageSenderService;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HelpCommandHandler implements CommandHandler {

    private static final String HELP_TEXT =
            """
            Доступные команды:
            /start   — регистрация бота
            /help    — помощь
            /track   <url> — начать отслеживание
            /untrack <url> — прекратить отслеживание
            /list    — показать все ваши ссылки
            """;
    private final MessageSenderService sender;

    @Override
    public boolean supports(Update u) {
        return u.message() != null && "/help".equals(u.message().text().trim());
    }

    @Override
    public void handle(Update u) {
        sender.send(u.message().chat().id(), HELP_TEXT);
    }
}
