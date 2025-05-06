package backend.academy.scrapper.notification;

import backend.academy.scrapper.model.LinkUpdate;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@org.junit.jupiter.api.extension.ExtendWith(MockitoExtension.class)
public class HttpNotificationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private HttpNotificationService notificationService;

    private LinkUpdate linkUpdate;

    @BeforeEach
    public void setUp() {
        linkUpdate = new LinkUpdate(
            1L,
            "http://example.com",
            "Обновление",
            Set.of(123L, 456L)
        );
    }

    @Test
    void notify_shouldSendPostRequestSuccessfully() {
        // Arrange
        when(restTemplate.postForEntity(
            eq("http://bot:8080/updates"),
            any(HttpEntity.class),
            eq(Void.class)
        )).thenReturn(ResponseEntity.ok().build());

        // Act
        notificationService.notify(linkUpdate);

        // Assert
        verify(restTemplate, times(1)).postForEntity(
            eq("http://bot:8080/updates"),
            any(HttpEntity.class),
            eq(Void.class)
        );
    }

    @Test
    void notify_shouldThrowExceptionOnFailure() {
        // Arrange
        when(restTemplate.postForEntity(
            eq("http://bot:8080/updates"),
            any(HttpEntity.class),
            eq(Void.class)
        )).thenThrow(new RuntimeException("Connection error"));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> notificationService.notify(linkUpdate));
        verify(restTemplate, times(1)).postForEntity(
            eq("http://bot:8080/updates"),
            any(HttpEntity.class),
            eq(Void.class)
        );
    }

    @Test
    void notify_shouldUseJsonContentType() {
        ArgumentCaptor<HttpEntity<LinkUpdate>> captor = ArgumentCaptor.forClass(HttpEntity.class);

        when(restTemplate.postForEntity(
            eq("http://bot:8080/updates"),
            any(HttpEntity.class),
            eq(Void.class)
        )).thenReturn(ResponseEntity.ok().build());

        notificationService.notify(linkUpdate);

        verify(restTemplate).postForEntity(eq("http://bot:8080/updates"), captor.capture(), eq(Void.class));

        HttpHeaders headers = captor.getValue().getHeaders();
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
    }

}
