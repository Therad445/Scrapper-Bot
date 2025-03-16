package backend.academy.scrapper.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.scrapper.service.ChatService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

class ChatControllerTest {

    private final ChatService chatService = mock(ChatService.class);
    private final ChatController chatController = new ChatController(chatService);
    private final MockMvc mockMvc =
            MockMvcBuilders.standaloneSetup(chatController).build();

    @Test
    void shouldRegisterChat() throws Exception {
        // Arrange
        Long chatId = 12345L;
        doNothing().when(chatService).register(chatId);

        // Act & Assert
        mockMvc.perform(post("/tg-chat/{id}", chatId))
                .andExpect(status().isOk())
                .andExpect(content().string("Чат зарегистрирован"));

        verify(chatService, times(1)).register(chatId);
    }

    @Test
    void shouldThrowExceptionWhenChatAlreadyRegistered() throws Exception {
        // Arrange
        Long chatId = 12345L;
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, ""))
                .when(chatService)
                .register(chatId);

        // Act & Assert
        mockMvc.perform(post("/tg-chat/{id}", chatId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));

        verify(chatService, times(1)).register(chatId);
    }

    @Test
    void shouldDeleteChat() throws Exception {
        // Arrange
        Long chatId = 12345L;
        doNothing().when(chatService).delete(chatId);

        // Act & Assert
        mockMvc.perform(delete("/tg-chat/{id}", chatId))
                .andExpect(status().isOk())
                .andExpect(content().string("Чат успешно удалён"));

        verify(chatService, times(1)).delete(chatId);
    }

    @Test
    void shouldThrowExceptionWhenChatDoesNotExistOnDelete() throws Exception {
        // Arrange
        Long chatId = 12345L;
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, ""))
                .when(chatService)
                .delete(chatId);

        // Act & Assert
        mockMvc.perform(delete("/tg-chat/{id}", chatId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));

        verify(chatService, times(1)).delete(chatId);
    }
}
