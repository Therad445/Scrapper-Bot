package backend.academy.scrapper.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ChatRepositoryTest {

    @Test
    void shouldRegisterChatId() {
        // Arrange
        ChatRepository chatRepository = new ChatRepository();
        Long chatId = 12345L;

        // Act
        chatRepository.register(chatId);

        // Assert
        assertThat(chatRepository.exists(chatId)).isTrue();
    }

    @Test
    void shouldDeleteChatId() {
        // Arrange
        ChatRepository chatRepository = new ChatRepository();
        Long chatId = 12345L;
        chatRepository.register(chatId);

        // Act
        chatRepository.delete(chatId);

        // Assert
        assertThat(chatRepository.exists(chatId)).isFalse();
    }

    @Test
    void shouldReturnFalseIfChatIdDoesNotExist() {
        // Arrange
        ChatRepository chatRepository = new ChatRepository();
        Long chatId = 12345L;

        // Act
        boolean exists = chatRepository.exists(chatId);

        // Assert
        assertThat(exists).isFalse();
    }
}
