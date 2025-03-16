package backend.academy.bot.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.bot.dto.LinkUpdate;
import backend.academy.bot.telegram.TelegramBotService;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class BotControllerTest {

    @InjectMocks
    private BotController botController;

    @Mock
    private TelegramBotService telegramBotService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(botController).build();
    }

    @Test
    void testSendNotificationValidLinkUpdate() throws Exception {
        // Arrange
        Set<Long> tgChatIds = new HashSet<>();
        tgChatIds.add(123L);
        LinkUpdate linkUpdate = new LinkUpdate(1L, "http://example.com", "Test description", tgChatIds);

        // Act & Assert
        mockMvc.perform(
                        post("/updates")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{ \"id\": 1, \"url\": \"http://example.com\", \"description\": \"Test description\", \"tgChatIds\": [123] }"))
                .andExpect(status().isOk())
                .andExpect(content().string("Обновление обработано"));

        verify(telegramBotService, times(1)).sendMessage(1L, "Обновилась ссылка http://example.com");
    }

    @Test
    void testSendNotificationInvalidLinkUpdate() throws Exception {
        // Act & Assert
        mockMvc.perform(
                        post("/updates")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{ \"id\": null, \"url\": \"http://example.com\", \"description\": \"Test description\", \"tgChatIds\": [123] }"))
                .andExpect(status().isBadRequest());
        verify(telegramBotService, never()).sendMessage(any(), any());
    }

    @Test
    void testSendNotificationMissingUrl() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/updates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"id\": 1, \"description\": \"Test description\", \"tgChatIds\": [123] }"))
                .andExpect(status().isBadRequest());
        verify(telegramBotService, never()).sendMessage(any(), any());
    }

    @Test
    void testSendNotificationInvalidUrl() throws Exception {
        // Act & Assert
        mockMvc.perform(
                        post("/updates")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{ \"id\": 1, \"url\": \"invalid-url\", \"description\": \"Test description\", \"tgChatIds\": [123] }"))
                .andExpect(status().isBadRequest());
        verify(telegramBotService, never()).sendMessage(any(), any());
    }

    @Test
    void testSendNotificationInvalidJson() throws Exception {
        // Act & Assert
        mockMvc.perform(
                        post("/updates")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{ \"id\": 1, \"url\": \"http://example.com\", \"description\": \"Test description\" }"))
                .andExpect(status().isBadRequest());

        verify(telegramBotService, never()).sendMessage(any(), any());
    }
}
