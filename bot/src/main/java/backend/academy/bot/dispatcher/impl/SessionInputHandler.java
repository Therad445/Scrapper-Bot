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
import java.util.OptionalLong;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

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
        OptionalLong cidOpt = CommandHandler.chatId(u);
        if (cidOpt.isEmpty()) {
            return false;
        }
        long cid = cidOpt.getAsLong();
        return sessionService.getSession(cid).getState() != BotState.NONE;
    }

    @Override
    public void handle(Update u) {
        OptionalLong cidOpt = CommandHandler.chatId(u);
        if (cidOpt.isEmpty()) return;
        long chatId = cidOpt.getAsLong();
        String text = u.message().text().trim();
        UserSession session = sessionService.getSession(chatId);

        if (session.getState() == BotState.WAITING_FOR_TAGS) {
            session.setPendingTags(text.isBlank() ? null : text);
            session.setState(BotState.WAITING_FOR_FILTERS);
            sessionService.saveSession(chatId, session);
            sender.send(chatId, "Введите фильтры (опционально):");

        } else if (session.getState() == BotState.WAITING_FOR_FILTERS) {
            session.setPendingFilters(text.isBlank() ? null : text);

            List<String> tags = session.getPendingTags() != null
                    ? Arrays.asList(session.getPendingTags().split("\\s+"))
                    : Collections.emptyList();
            List<String> filters =
                    session.getPendingFilters() != null ? Arrays.asList(text.split("\\s+")) : Collections.emptyList();

            try {
                linkService.track(chatId, URI.create(session.getPendingUrl()), tags, filters);
                sender.send(chatId, "Ссылка добавлена с тэгами и фильтрами ✅");
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                    sender.send(chatId, "Scrapper отклонил ссылку как некорректную.");
                } else {
                    sender.send(chatId, "Не удалось добавить ссылку: " + e.getMessage());
                }
            }
            session.reset();
            sessionService.deleteSession(chatId);
        }
    }
}
