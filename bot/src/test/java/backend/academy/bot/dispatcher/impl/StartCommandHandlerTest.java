package backend.academy.bot.dispatcher.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.bot.service.ChatService;
import backend.academy.bot.service.MessageSenderService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StartCommandHandlerTest {

    private final Long chatId = 123L;
    private ChatService chatService;
    private MessageSenderService sender;
    private StartCommandHandler handler;

    @BeforeEach
    void setUp() {
        chatService = mock(ChatService.class);
        sender = mock(MessageSenderService.class);
        handler = new StartCommandHandler(chatService, sender);
    }

    private Update mockUpdate(String text) {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn(text);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(chatId);

        return update;
    }

    @Test
    void supports_ShouldReturnTrue_WhenMessageIsStart() {
        Update update = mockUpdate("/start");

        assert handler.supports(update);
    }

    @Test
    void supports_ShouldReturnFalse_WhenMessageIsNotStart() {
        Update update = mockUpdate("/unknown");

        assert !handler.supports(update);
    }

    @Test
    void handle_ShouldRegisterChatAndSendGreeting() {
        Update update = mockUpdate("/start");

        handler.handle(update);

        verify(chatService).register(chatId);
        verify(sender).send(chatId, "Привет! Я слежу за ссылками. Введите /help для списка команд.");
    }
}
