// File: bot/src/main/java/backend/academy/bot/dispatcher/impl/TrackCommandHandler.java
package backend.academy.bot.dispatcher.impl;

import backend.academy.bot.config.BotProperties;
import backend.academy.bot.dto.command.TrackCommand;
import backend.academy.bot.dto.command.BotCommandMessage;
import backend.academy.bot.dispatcher.CommandHandler;
import backend.academy.bot.service.MessageSenderService;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;

import java.net.URI;

@RequiredArgsConstructor
public class TrackCommandHandler implements CommandHandler {

    private final KafkaTemplate<String, BotCommandMessage> kafkaTemplate;
    private final BotProperties botProps;
    private final MessageSenderService sender;

    @Override
    public boolean supports(Update u) {
        return u.message() != null && u.message().text().startsWith("/track ");
    }

    @Override
    public void handle(Update u) {
        Long chatId = u.message().chat().id();
        String url = u.message().text().substring(7).trim();

        TrackCommand cmd = new TrackCommand(chatId, URI.create(url), java.util.List.of(), java.util.List.of());
        kafkaTemplate.send(botProps.getKafka().getCommandsTopic(), cmd);
        sender.send(chatId, "Команда отправлена на сервер, ожидайте подтверждения...");
    }
}
