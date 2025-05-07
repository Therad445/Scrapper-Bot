package backend.academy.bot.dispatcher;

import com.pengrad.telegrambot.model.Update;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommandDispatcher {

    private final List<CommandHandler> handlers;

    public void dispatch(Update update) {
        handlers.stream()
                .filter(h -> h.supports(update))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Unknown command"))
                .handle(update);
    }
}
