package backend.academy.bot.dispatcher.impl;

import backend.academy.bot.dispatcher.CommandHandler;
import backend.academy.bot.service.LinkService;
import backend.academy.bot.service.MessageSenderService;
import backend.academy.bot.service.SessionService;
import backend.academy.bot.state.BotState;
import backend.academy.bot.state.UserSession;
import com.pengrad.telegrambot.model.Update;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class SessionInputHandler implements CommandHandler {

    private final SessionService sessionService;
    private final LinkService linkService;
    private final MessageSenderService sender;

    public SessionInputHandler(SessionService sessionService, LinkService linkService, MessageSenderService sender) {
        this.sessionService = sessionService;
        this.linkService = linkService;
        this.sender = sender;
    }

    @Override
    public boolean supports(Update u) {
        Long chatId = u.message().chat().id();
        return sessionService.getSession(chatId).state() != BotState.NONE;
    }

    @Override
    public void handle(Update u) {
        Long chatId = u.message().chat().id();
        String text = u.message().text().trim();
        UserSession session = sessionService.getSession(chatId);

        if (session.state() == BotState.WAITING_FOR_TAGS) {
            session.pendingTags(text.isBlank() ? null : text);
            session.state(BotState.WAITING_FOR_FILTERS);
            sender.send(chatId, "Введите фильтры (опционально):");
        } else if (session.state() == BotState.WAITING_FOR_FILTERS) {
            session.pendingFilters(text.isBlank() ? null : text);
            List<String> tags = session.pendingTags() != null
                ? Arrays.asList(session.pendingTags().split("\\s+"))
                : Collections.emptyList();
            List<String> filters = session.pendingFilters() != null
                ? Arrays.asList(text.split("\\s+"))
                : Collections.emptyList();
            linkService.track(chatId, URI.create(session.pendingUrl()), tags, filters);
            sender.send(chatId, "Ссылка добавлена с тэгами и фильтрами ✅");
            session.reset();
        }
    }
}
