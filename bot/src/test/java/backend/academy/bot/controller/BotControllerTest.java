package backend.academy.bot.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.bot.dto.LinkUpdate;
import backend.academy.bot.service.MessageSenderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = BotController.class)
@Import(BotControllerTest.TestConfig.class)
class BotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSenderService sender;

    @Test
    void sendNotification_ShouldSendMessagesAndReturnOk() throws Exception {
        LinkUpdate update = new LinkUpdate(312312L, "https://example.com", "New update available", Set.of(123L, 456L));

        mockMvc.perform(post("/updates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(content().string("Обновление обработано"));

        String expectedMsg = "Обновилась ссылка: https://example.com\nОписание: New update available";
        verify(sender).send(123L, expectedMsg);
        verify(sender).send(456L, expectedMsg);
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public MessageSenderService messageSenderService() {
            return mock(MessageSenderService.class);
        }
    }
}
