package backend.academy.bot.dispatcher.impl;

import backend.academy.bot.dispatcher.CommandHandler;
import backend.academy.bot.service.MessageSenderService;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@RequiredArgsConstructor
public class UnknownCommandHandler implements CommandHandler {

    private final MessageSenderService sender;

    @Override
    public boolean supports(Update u) {
        return u.message() != null;
    }

    @Override
    public void handle(Update u) {
        sender.send(
                u.message().chat().id(), "Неизвестная команда. Введите /help, чтобы увидеть список доступных команд.");
    }
}
