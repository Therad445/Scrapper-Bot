// File: bot/src/main/java/backend/academy/bot/dispatcher/impl/ListCommandHandler.java
package backend.academy.bot.dispatcher.impl;

import backend.academy.bot.config.BotProperties;
import backend.academy.bot.dto.command.ListCommand;
import backend.academy.bot.dto.command.BotCommandMessage;
import backend.academy.bot.dispatcher.CommandHandler;
import backend.academy.bot.service.MessageSenderService;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;

@RequiredArgsConstructor
public class ListCommandHandler implements CommandHandler {

    private final KafkaTemplate<String, BotCommandMessage> kafkaTemplate;
    private final BotProperties botProps;
    private final MessageSenderService sender;

    @Override
    public boolean supports(Update u) {
        return u.message() != null && "/list".equals(u.message().text().trim());
    }

    @Override
    public void handle(Update u) {
        Long chatId = u.message().chat().id();

        ListCommand cmd = new ListCommand(chatId);
        kafkaTemplate.send(botProps.getKafka().getCommandsTopic(), cmd);
        sender.send(chatId, "Команда отправлена на сервер, ожидайте подтверждения...");
    }
}
