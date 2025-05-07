package backend.academy.bot.dispatcher.impl;

import backend.academy.bot.dispatcher.CommandHandler;
import backend.academy.bot.dto.LinkResponse;
import backend.academy.bot.service.LinkService;
import backend.academy.bot.service.MessageSenderService;
import com.pengrad.telegrambot.model.Update;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListCommandHandler implements CommandHandler {

    private final LinkService linkService;
    private final MessageSenderService sender;

    @Override
    public boolean supports(Update u) {
        return u.message() != null && "/list".equals(u.message().text().trim());
    }

    @Override
    public void handle(Update u) {
        Long chatId = u.message().chat().id();
        List<LinkResponse> links = linkService.list(chatId);

        if (links.isEmpty()) {
            sender.send(chatId, "Вы ничего не отслеживаете 🤷‍♂️");
            return;
        }

        String body = links.stream()
            .map(l -> l.getLink().toString())
            .collect(Collectors.joining("\n"));

        sender.send(chatId, "Ваши подписки:\n" + body);
    }
}
