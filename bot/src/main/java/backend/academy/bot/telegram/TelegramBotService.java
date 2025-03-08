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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

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
            new BotCommand("/track", "Отслеживать ссылку"),
            new BotCommand("/untrack", "Прекратить отслеживание ссылки"),
            new BotCommand("/list", "Список ссылок")
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
        if (parts.length < 2 || parts[1].isBlank()) {
            sendMessage(chatId, "Введите URL для отслеживания после команды /track");
            return;
        }
        String url = parts[1];
        session.setPendingUrl(url);
        session.setState(BotState.WAITING_FOR_TAGS);
        sendMessage(chatId, "Введите тэги (опционально):");
    }

    private void handleUntrack(Long chatId, String text) {
        String[] parts = text.split(" ", 2);
        if (parts.length < 2 || parts[1].isBlank()) {
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
            String tagsInput = text.trim().isEmpty() ? "Нет" : text;
            session.setPendingTags(tagsInput);
            session.setState(BotState.WAITING_FOR_FILTERS);
            sendMessage(chatId, "Настройте фильтры (опционально):");
        } else if (session.getState() == BotState.WAITING_FOR_FILTERS) {
            String filtersInput = text.trim().isEmpty() ? "Нет" : text;
            session.setPendingFilters(filtersInput);
            List<String> tags = !session.getPendingTags().equals("Нет") ?
                Arrays.asList(session.getPendingTags().split("\\s+")) : Collections.emptyList();
            List<String> filters = !filtersInput.equals("Нет") ?
                Arrays.asList(filtersInput.split("\\s+")) : Collections.emptyList();
            LinkResponse response = scrapperClient.trackLink(chatId, session.getPendingUrl(), tags, filters);
            sendMessage(chatId, "Ссылка добавлена в отслеживание: " + response.getLink());
            session.reset();
        }
    }


    public void sendMessage(Long chatId, String text) {
        telegramBot.execute(new SendMessage(chatId, text));
    }
}
