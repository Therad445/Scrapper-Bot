package backend.academy.scrapper.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.scrapper.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChatController.class)
@Import(ChatControllerTest.MockConfig.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = "app.bot-url=http://localhost:8080")

class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChatService chatService;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(chatService);
    }

    @Test
    @DisplayName("POST /tg-chat/{id} - valid ID")
    void registerChat_validId_shouldReturnOk() throws Exception {
        long chatId = 123;
        mockMvc.perform(post("/tg-chat/{id}", chatId)).andExpect(status().isOk());

        verify(chatService).register(chatId);
    }

    @Test
    @DisplayName("DELETE /tg-chat/{id} - valid ID")
    void deleteChat_validId_shouldReturnOk() throws Exception {
        long chatId = 456;
        mockMvc.perform(delete("/tg-chat/{id}", chatId)).andExpect(status().isOk());

        verify(chatService).delete(chatId);
    }

    @Test
    @DisplayName("POST /tg-chat/{id} - invalid (negative) ID")
    void registerChat_invalidId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/tg-chat/{id}", -1)).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /tg-chat/{id} - invalid (zero) ID")
    void deleteChat_zeroId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(delete("/tg-chat/{id}", 0)).andExpect(status().isBadRequest());
    }

    @TestConfiguration
    static class MockConfig {
        @Bean
        public ChatService chatService() {
            return mock(ChatService.class);
        }
    }
}
