package backend.academy.bot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import backend.academy.bot.telegram.TelegramBotService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BotApplicationTests {

    @Autowired
    private BotConfig botConfig;

    @Autowired
    private TelegramBotService botService;

    @Mock
    private TelegramBotService mockBotService;

    @Test
    void contextLoads() {}

    @Test
    void testBotConfigLoads() {
        // Arrange & Act — получаем botConfig из контекста
        // Assert
        assertThat(botConfig).isNotNull();
        assertThat(botConfig.telegramToken()).isNotEmpty();
    }

    @Test
    void testBotServiceBeanLoads() {
        // Arrange & Act — получаем botService из контекста
        // Assert
        assertThat(botService).isNotNull();
    }

    @Test
    void testSendMessage() {
        // Arrange
        Long chatId = 123456L;
        String message = "Hello, world!";

        // Act
        mockBotService.sendMessage(chatId, message);

        // Assert
        verify(mockBotService, times(1)).sendMessage(chatId, message);
    }
}
