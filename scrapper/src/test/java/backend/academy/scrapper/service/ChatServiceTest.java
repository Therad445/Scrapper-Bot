package backend.academy.scrapper.service;

import backend.academy.scrapper.repository.ChatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChatServiceTest {

    private ChatRepository chatRepository;
    private ChatService chatService;

    @BeforeEach
    void setUp() {
        chatRepository = mock(ChatRepository.class);
        chatService = new ChatService(chatRepository);
    }

    @Test
    void register_shouldCallRegister_whenChatDoesNotExist() {
        long chatId = 123L;
        when(chatRepository.exists(chatId)).thenReturn(false);

        chatService.register(chatId);

        verify(chatRepository).register(chatId);
    }

    @Test
    void register_shouldDoNothing_whenChatExists() {
        long chatId = 456L;
        when(chatRepository.exists(chatId)).thenReturn(true);

        chatService.register(chatId);

        verify(chatRepository, never()).register(anyLong());
    }


    @Test
    void delete_shouldCallDelete_whenChatExists() {
        long chatId = 789L;
        when(chatRepository.exists(chatId)).thenReturn(true);

        chatService.delete(chatId);

        verify(chatRepository).delete(chatId);
    }

    @Test
    void delete_shouldThrow404_whenChatDoesNotExist() {
        long chatId = 999L;
        when(chatRepository.exists(chatId)).thenReturn(false);

        var ex = assertThrows(ResponseStatusException.class, () -> chatService.delete(chatId));

        assertEquals(404, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("Чат не существует"));
    }
}
