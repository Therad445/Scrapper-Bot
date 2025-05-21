package backend.academy.bot.dispatcher.impl;

import backend.academy.bot.dispatcher.BotCommand;
import backend.academy.bot.dispatcher.CommandHandler;
import backend.academy.bot.dto.LinkResponse;
import backend.academy.bot.service.LinkService;
import backend.academy.bot.service.MessageSenderService;
import backend.academy.bot.service.SessionService;
import backend.academy.bot.state.BotState;
import backend.academy.bot.state.UserSession;
import com.pengrad.telegrambot.model.Update;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@BotCommand
public class TrackCommandHandler implements CommandHandler {

    private final LinkService linkService;
    private final MessageSenderService sender;
    private final SessionService sessionService;

    @Override
    public boolean supports(Update u) {
        return u.message() != null && u.message().text().startsWith("/track ");
    }

    @Override
    public void handle(Update u) {
        Long chatId = u.message().chat().id();
        String url = u.message().text().substring(7).trim();

        List<LinkResponse> existing = linkService.list(chatId);
        if (existing.stream().anyMatch(l -> l.getLink().toString().equals(url))) {
            sender.send(chatId, "Ссылка уже отслеживается ✅");
            return;
        }

        UserSession session = sessionService.getSession(chatId);
        session.pendingUrl(url);
        session.state(BotState.WAITING_FOR_TAGS);
        sender.send(chatId, "Введите тэги (опционально):");
    }
}
