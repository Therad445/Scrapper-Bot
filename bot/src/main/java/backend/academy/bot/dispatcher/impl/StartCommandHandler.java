package backend.academy.bot.dispatcher.impl;

import backend.academy.bot.dispatcher.BotCommand;
import backend.academy.bot.dispatcher.CommandHandler;
import backend.academy.bot.service.ChatService;
import backend.academy.bot.service.MessageSenderService;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@BotCommand
public class StartCommandHandler implements CommandHandler {

    private final ChatService chatService;
    private final MessageSenderService sender;

    @Override
    public boolean supports(Update u) {
        return u.message() != null && "/start".equals(u.message().text());
    }

    @Override
    public void handle(Update u) {
        Long chatId = u.message().chat().id();
        chatService.register(chatId);
        sender.send(chatId, "Привет! Я слежу за ссылками. Введите /help для списка команд.");
    }
}
