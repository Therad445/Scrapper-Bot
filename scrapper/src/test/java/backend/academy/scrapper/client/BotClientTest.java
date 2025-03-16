package backend.academy.scrapper.client;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import backend.academy.scrapper.model.LinkUpdate;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class BotClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BotClient botClient;

    private Long id;
    private String url;
    private String description;
    private Set<Long> tgChatIds;
    private LinkUpdate linkUpdate;

    @BeforeEach
    public void setUp() {
        id = 1L;
        url = "http://example.com";
        description = "Update description";
        tgChatIds = new HashSet<>();
        tgChatIds.add(123L);
        tgChatIds.add(456L);

        linkUpdate = new LinkUpdate(id, url, description, tgChatIds);
    }

    @Test
    public void testNotifyUpdate_Success() {
        // Arrange
        ResponseEntity<String> mockResponse = ResponseEntity.ok("Success");
        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponse);

        // Act
        Object response = botClient.notifyUpdate(id, url, description, tgChatIds);

        // Assert
        assertEquals("Success", response);
        verify(restTemplate, times(1))
                .exchange(any(URI.class), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }

    @Test
    public void testNotifyUpdate_Failure() {
        // Arrange
        ResponseEntity<String> mockResponse = ResponseEntity.status(500).body("Error");
        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponse);

        // Act
        Object response = botClient.notifyUpdate(id, url, description, tgChatIds);

        // Assert
        assertEquals("Error", response);
        verify(restTemplate, times(1))
                .exchange(any(URI.class), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }

    @Test
    public void testNotifyUpdate_InvalidUri() {
        // Arrange
        String invalidUrl = "http://invalid-url";
        Set<Long> invalidTgChatIds = new HashSet<>();
        invalidTgChatIds.add(-1L); // Добавляем некорректный chatId

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            botClient.notifyUpdate(id, invalidUrl, description, invalidTgChatIds);
        });
    }
}
