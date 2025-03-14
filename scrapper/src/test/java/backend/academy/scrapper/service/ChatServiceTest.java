package backend.academy.scrapper.service;

import backend.academy.scrapper.repository.ChatRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ChatServiceTest {

    @Test
    void shouldRegisterChatWhenNotExists() {
        // Arrange
        ChatRepository chatRepository = mock(ChatRepository.class);
        ChatService chatService = new ChatService(chatRepository);
        Long chatId = 12345L;

        when(chatRepository.exists(chatId)).thenReturn(false);

        // Act
        chatService.register(chatId);

        // Assert
        verify(chatRepository, times(1)).register(chatId);
    }

    @Test
    void shouldThrowExceptionWhenChatAlreadyRegistered() {
        // Arrange
        ChatRepository chatRepository = mock(ChatRepository.class);
        ChatService chatService = new ChatService(chatRepository);
        Long chatId = 12345L;

        when(chatRepository.exists(chatId)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> chatService.register(chatId))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Чат уже зарегистрирован")
            .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldDeleteChatWhenExists() {
        // Arrange
        ChatRepository chatRepository = mock(ChatRepository.class);
        ChatService chatService = new ChatService(chatRepository);
        Long chatId = 12345L;

        when(chatRepository.exists(chatId)).thenReturn(true);

        // Act
        chatService.delete(chatId);

        // Assert
        verify(chatRepository, times(1)).delete(chatId);
    }

    @Test
    void shouldThrowExceptionWhenChatDoesNotExist() {
        // Arrange
        ChatRepository chatRepository = mock(ChatRepository.class);
        ChatService chatService = new ChatService(chatRepository);
        Long chatId = 12345L;

        when(chatRepository.exists(chatId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> chatService.delete(chatId))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Чат не существует")
            .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
    }
}
