package backend.academy.bot.dispatcher.impl;

import backend.academy.bot.dispatcher.BotCommand;
import backend.academy.bot.dispatcher.CommandHandler;
import backend.academy.bot.service.LinkService;
import backend.academy.bot.service.MessageSenderService;
import com.pengrad.telegrambot.model.Update;
import java.net.URI;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@BotCommand
public class UntrackCommandHandler implements CommandHandler {

    private final LinkService linkService;
    private final MessageSenderService sender;

    @Override
    public boolean supports(Update u) {
        return u.message() != null && u.message().text().startsWith("/untrack ");
    }

    @Override
    public void handle(Update u) {
        Long chatId = u.message().chat().id();
        String url = u.message().text().substring(9).trim();

        try {
            linkService.untrack(chatId, URI.create(url));
            sender.send(chatId, "Ссылка больше не отслеживается");
        } catch (Exception ex) {
            sender.send(chatId, "Не удалось удалить ссылку: " + ex.getMessage());
        }
    }
}
