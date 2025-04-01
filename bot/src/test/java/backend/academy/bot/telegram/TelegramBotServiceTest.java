package backend.academy.bot.telegram;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import backend.academy.bot.BotConfig;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.dto.LinkResponse;
import backend.academy.bot.dto.ListLinksResponse;
import backend.academy.bot.state.BotState;
import backend.academy.bot.state.UserSession;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TelegramBotServiceTest {

    private TestableTelegramBotService botService;
    private ScrapperClient scrapperClient;
    private BotConfig botConfig;

    @BeforeEach
    public void setup() {
        scrapperClient = mock(ScrapperClient.class);
        botConfig = mock(BotConfig.class);
        when(botConfig.telegramToken()).thenReturn("dummy-token");
        botService = new TestableTelegramBotService(botConfig, scrapperClient);
    }

    @Test
    public void testTrackCommandParsesLinkCorrectly() throws Exception {
        Long chatId = 123L;
        String trackCommand = "/track https://example.com";
        botService.processText(chatId, trackCommand);

        UserSession session = botService.getUserSession(chatId);
        assertEquals("https://example.com", session.getPendingUrl());
        assertEquals(BotState.WAITING_FOR_TAGS, session.getState());

        List<String> messages = botService.getSentMessages().get(chatId);
        assertTrue(messages.get(messages.size() - 1).contains("Введите тэги"));
    }

    @Test
    public void testUnknownCommandSendsErrorMessage() throws Exception {
        Long chatId = 456L;
        String unknownCommand = "/unknown";
        botService.processText(chatId, unknownCommand);
        List<String> messages = botService.getSentMessages().get(chatId);
        assertTrue(messages.get(messages.size() - 1).contains("Неизвестная команда"));
    }

    @Test
    public void testListCommandFormattingEmpty() throws Exception {
        Long chatId = 789L;
        when(scrapperClient.getLinks(chatId)).thenReturn(new ListLinksResponse(Collections.emptyList(), 0));
        botService.processText(chatId, "/list");
        List<String> messages = botService.getSentMessages().get(chatId);
        assertTrue(messages.get(messages.size() - 1).contains("Список отслеживаемых ссылок пуст."));
    }

    @Test
    public void testListCommandFormattingWithLinks() throws Exception {
        Long chatId = 101L;
        LinkResponse linkResponse =
                new LinkResponse(1L, "https://example.com", Collections.emptyList(), Collections.emptyList());
        when(scrapperClient.getLinks(chatId))
                .thenReturn(new backend.academy.bot.dto.ListLinksResponse(List.of(linkResponse), 1));
        botService.processText(chatId, "/list");
        List<String> messages = botService.getSentMessages().get(chatId);
        assertTrue(messages.get(messages.size() - 1).contains("https://example.com"));
    }

    private static class TestableTelegramBotService extends TelegramBotService {
        private final Map<Long, List<String>> sentMessages = new java.util.HashMap<>();

        public TestableTelegramBotService(BotConfig config, ScrapperClient scrapperClient) {
            super(config, scrapperClient);
        }

        @Override
        public void sendMessage(Long chatId, String text) {
            sentMessages
                    .computeIfAbsent(chatId, k -> new java.util.ArrayList<>())
                    .add(text);
        }

        public void processText(Long chatId, String text) throws Exception {
            Update update = createUpdate(chatId, text);
            invokeProcessUpdate(update);
        }

        private Update createUpdate(Long chatId, String text) {
            Update update = mock(Update.class);
            Message message = mock(Message.class);
            Chat chat = mock(Chat.class);
            when(chat.id()).thenReturn(chatId);
            when(message.chat()).thenReturn(chat);
            when(message.text()).thenReturn(text);
            when(update.message()).thenReturn(message);
            return update;
        }

        private void invokeProcessUpdate(Update update) {
            try {
                Method method = TelegramBotService.class.getDeclaredMethod("processUpdate", Update.class);
                method.setAccessible(true);
                method.invoke(this, update);
            } catch (Exception e) {
                throw new RuntimeException("Ошибка вызова processUpdate через рефлексию", e);
            }
        }

        public Map<Long, List<String>> getSentMessages() {
            return sentMessages;
        }

        public UserSession getUserSession(Long chatId) {
            try {
                Field field = TelegramBotService.class.getDeclaredField("userSessions");
                field.setAccessible(true);
                Map<Long, UserSession> sessions = (Map<Long, UserSession>) field.get(this);
                return sessions.computeIfAbsent(chatId, k -> new UserSession());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Ошибка доступа к userSessions", e);
            }
        }
    }
}
