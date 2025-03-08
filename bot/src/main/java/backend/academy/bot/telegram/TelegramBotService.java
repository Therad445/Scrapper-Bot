package backend.academy.bot.telegram;

import backend.academy.bot.BotConfig;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.dto.LinkResponse;
import backend.academy.bot.dto.ListLinksResponse;
import backend.academy.bot.state.BotState;
import backend.academy.bot.state.UserSession;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.model.BotCommand;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class TelegramBotService {

    private final TelegramBot telegramBot;
    private final ScrapperClient scrapperClient;
    private final Map<Long, UserSession> userSessions = new HashMap<>();

    public TelegramBotService(BotConfig config, ScrapperClient scrapperClient) {
        this.telegramBot = new TelegramBot(config.telegramToken());
        this.scrapperClient = scrapperClient;
    }

    @PostConstruct
    public void init() {
        setMyCommands();
        startListener();
        log.info("Telegram Bot запущен");
    }

    private void setMyCommands() {
        BotCommand[] commands = {
            new BotCommand("/start", "Регистрация пользователя"),
            new BotCommand("/help", "Список команд"),
            new BotCommand("/track", "Начать отслеживание ссылки"),
            new BotCommand("/untrack", "Прекратить отслеживание ссылки"),
            new BotCommand("/list", "Список отслеживаемых ссылок")
        };
        telegramBot.execute(new SetMyCommands(commands));
    }

    private void startListener() {
        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                processUpdate(update);
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void processUpdate(Update update) {
        Message message = update.message();
        if (message == null || message.text() == null) return;

        Long chatId = message.chat().id();
        String text = message.text().trim();
        log.info("Получено сообщение от чата {}: {}", chatId, text);

        UserSession session = userSessions.computeIfAbsent(chatId, k -> new UserSession());
        if (session.getState() != BotState.NONE) {
            handleStatefulInput(chatId, text, session);
            return;
        }

        switch (text.split(" ")[0]) {
            case "/start" -> handleStart(chatId);
            case "/help" -> handleHelp(chatId);
            case "/track" -> handleTrack(chatId, text, session);
            case "/untrack" -> handleUntrack(chatId, text);
            case "/list" -> handleList(chatId);
            default -> sendMessage(chatId, "Неизвестная команда. Используйте /help для списка команд.");
        }
    }

    private void handleStart(Long chatId) {
        scrapperClient.registerUser(chatId);
        sendMessage(chatId, "Вы успешно зарегистрированы! Используйте /help для списка команд.");
    }

    private void handleHelp(Long chatId) {
        sendMessage(chatId, "Доступные команды:\n"
            + "/start - регистрация\n"
            + "/help - помощь\n"
            + "/track - отслеживать ссылку\n"
            + "/untrack - прекратить отслеживание ссылки\n"
            + "/list - список ссылок");
    }

    private void handleTrack(Long chatId, String text, UserSession session) {
        String[] parts = text.split(" ", 2);
        if (parts.length < 2) {
            sendMessage(chatId, "Введите URL для отслеживания после команды /track");
            return;
        }
        String url = parts[1];
        LinkResponse response = scrapperClient.trackLink(chatId, url);
        sendMessage(chatId, "Ссылка добавлена в отслеживание: " + response.getUrl());
    }

    private void handleUntrack(Long chatId, String text) {
        String[] parts = text.split(" ", 2);
        if (parts.length < 2) {
            sendMessage(chatId, "Введите URL для удаления после команды /untrack");
            return;
        }
        String url = parts[1];
        scrapperClient.untrackLink(chatId, url);
        sendMessage(chatId, "Ссылка удалена из отслеживания: " + url);
    }

    private void handleList(Long chatId) {
        ListLinksResponse response = scrapperClient.getLinks(chatId);
        if (response == null || response.getLinks().isEmpty()) {
            sendMessage(chatId, "Список отслеживаемых ссылок пуст.");
        } else {
            StringBuilder sb = new StringBuilder("Ваши подписки:\n");
            response.getLinks().forEach(link -> sb.append(link.getLink()).append("\n"));
            sendMessage(chatId, sb.toString());
        }
    }

    private void handleStatefulInput(Long chatId, String text, UserSession session) {
        if (session.getState() == BotState.WAITING_FOR_TAGS) {
            session.setPendingTags(text);
            session.setState(BotState.WAITING_FOR_FILTERS);
            sendMessage(chatId, "Введите фильтры (опционально):");
        } else if (session.getState() == BotState.WAITING_FOR_FILTERS) {
            session.setPendingFilters(text);
            String responseMsg = String.format("Ссылка %s добавлена с тегами: %s и фильтрами: %s",
                session.getPendingUrl(),
                session.getPendingTags(),
                session.getPendingFilters());
            sendMessage(chatId, responseMsg);
            // Сброс состояния
            session.setState(BotState.NONE);
            session.setPendingUrl(null);
            session.setPendingTags(null);
            session.setPendingFilters(null);
        }
    }

    public void sendMessage(Long chatId, String text) {
        telegramBot.execute(new SendMessage(chatId, text));
    }
}
