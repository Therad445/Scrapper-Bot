package backend.academy.scrapper.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.scrapper.config.ScrapperProperties;
import backend.academy.scrapper.model.LinkUpdate;
import java.net.URI;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class HttpNotificationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ScrapperProperties scrapperProperties;

    @InjectMocks
    private HttpNotificationService notificationService;

    private LinkUpdate linkUpdate;

    @BeforeEach
    public void setUp() {
        linkUpdate = new LinkUpdate(1L, "http://example.com", "Обновление", Set.of(123L, 456L));
        when(scrapperProperties.botUrl()).thenReturn(URI.create("http://bot:8080"));
    }

    @Test
    void notify_shouldSendPostRequestSuccessfully() {
        when(restTemplate.postForEntity(eq("http://bot:8080/updates"), any(HttpEntity.class), eq(Void.class)))
            .thenReturn(ResponseEntity.ok().build());

        notificationService.notify(linkUpdate);

        verify(restTemplate, times(1))
            .postForEntity(eq("http://bot:8080/updates"), any(HttpEntity.class), eq(Void.class));
    }

    @Test
    void notify_shouldThrowExceptionOnFailure() {
        when(restTemplate.postForEntity(eq("http://bot:8080/updates"), any(HttpEntity.class), eq(Void.class)))
            .thenThrow(new RuntimeException("Connection error"));

        assertThrows(IllegalStateException.class, () -> notificationService.notify(linkUpdate));

        verify(restTemplate, times(1))
            .postForEntity(eq("http://bot:8080/updates"), any(HttpEntity.class), eq(Void.class));
    }

    @Test
    void notify_shouldUseJsonContentType() {
        ArgumentCaptor<HttpEntity<LinkUpdate>> captor = ArgumentCaptor.forClass(HttpEntity.class);

        when(restTemplate.postForEntity(eq("http://bot:8080/updates"), any(HttpEntity.class), eq(Void.class)))
            .thenReturn(ResponseEntity.ok().build());

        notificationService.notify(linkUpdate);

        verify(restTemplate).postForEntity(eq("http://bot:8080/updates"), captor.capture(), eq(Void.class));

        HttpHeaders headers = captor.getValue().getHeaders();
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
    }
}
