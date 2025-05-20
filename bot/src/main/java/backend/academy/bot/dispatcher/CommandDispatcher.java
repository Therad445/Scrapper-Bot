package backend.academy.bot.dispatcher;

import backend.academy.bot.dispatcher.impl.UnknownCommandHandler;
import com.pengrad.telegrambot.model.Update;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommandDispatcher {

    private final List<CommandHandler> handlers;

    private final UnknownCommandHandler unknown;

    public void dispatch(Update update) {
        handlers.stream()
                .filter(h -> h != unknown && h.supports(update))
                .findFirst()
                .orElse(unknown)
                .handle(update);
    }
}
