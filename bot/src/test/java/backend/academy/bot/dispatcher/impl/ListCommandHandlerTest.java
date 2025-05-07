package backend.academy.bot.dispatcher.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.bot.dto.LinkResponse;
import backend.academy.bot.service.LinkService;
import backend.academy.bot.service.MessageSenderService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ListCommandHandlerTest {

    private LinkService linkService;
    private MessageSenderService sender;
    private ListCommandHandler handler;

    @BeforeEach
    void setUp() {
        linkService = mock(LinkService.class);
        sender = mock(MessageSenderService.class);
        handler = new ListCommandHandler(linkService, sender);
    }

    private Update createUpdate(Long chatId, String text) {
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
    void supports_ShouldReturnTrue_ForListCommand() {
        Update update = createUpdate(123L, "/list");
        boolean result = handler.supports(update);
        assert result;
    }

    @Test
    void supports_ShouldReturnFalse_ForOtherCommand() {
        Update update = createUpdate(123L, "/help");
        boolean result = handler.supports(update);
        assert !result;
    }

    @Test
    void handle_ShouldSendEmptyMessage_WhenNoLinks() {
        Long chatId = 123L;
        Update update = createUpdate(chatId, "/list");

        when(linkService.list(chatId)).thenReturn(List.of());

        handler.handle(update);

        verify(sender).send(chatId, "Вы ничего не отслеживаете 🤷‍♂️");
    }

    @Test
    void handle_ShouldSendListMessage_WhenLinksExist() {
        Long chatId = 123L;
        Update update = createUpdate(chatId, "/list");

        List<LinkResponse> links = List.of(
                new LinkResponse(1L, URI.create("https://example.com/1"), List.of(), List.of()),
                new LinkResponse(2L, URI.create("https://example.com/2"), List.of(), List.of()));

        when(linkService.list(chatId)).thenReturn(links);

        handler.handle(update);

        verify(sender).send(chatId, "Ваши подписки:\nhttps://example.com/1\nhttps://example.com/2");
    }
}
