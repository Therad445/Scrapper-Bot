package backend.academy.bot.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.bot.BotConfig;
import backend.academy.bot.dto.LinkResponse;
import backend.academy.bot.dto.ListLinksResponse;
import java.net.URI;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class ScrapperClientTest {

    @Mock
    private RestTemplate restTemplate;

    private ScrapperClient client;

    @BeforeEach
    void setUp() {
        BotConfig mockConfig = new BotConfig("dummyToken", "http://scrapper:8081/");
        client = new ScrapperClient(restTemplate, mockConfig);
    }

    @Test
    void registerUser_shouldCallEndpoint() {
        Long chatId = 123L;
        URI uri = URI.create("http://scrapper:8081/tg-chat/" + chatId);
        when(restTemplate.postForEntity(eq(uri), isNull(), eq(Void.class)))
                .thenReturn(ResponseEntity.ok().build());

        client.registerUser(chatId);

        verify(restTemplate, times(1)).postForEntity(eq(uri), isNull(), eq(Void.class));
    }

    @Test
    void getLinks_shouldReturnListLinksResponse() {
        Long chatId = 123L;
        ListLinksResponse expected = new ListLinksResponse();
        when(restTemplate.exchange(
                        any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(ListLinksResponse.class)))
                .thenReturn(ResponseEntity.ok(expected));

        ListLinksResponse result = client.getLinks(chatId);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void trackLink_shouldReturnLinkResponse() {
        Long chatId = 123L;
        URI url = URI.create("https://example.com");
        LinkResponse expected = new LinkResponse(chatId, url, Collections.emptyList(), Collections.emptyList());

        when(restTemplate.postForEntity(any(), any(HttpEntity.class), eq(LinkResponse.class)))
                .thenReturn(ResponseEntity.ok(expected));

        LinkResponse result = client.trackLink(chatId, url);

        assertNotNull(result);
        assertEquals(expected.getLink(), result.getLink());
    }

    @Test
    void untrackLink_shouldReturnLinkResponse() {
        Long chatId = 123L;
        URI url = URI.create("https://example.com");
        LinkResponse expected = new LinkResponse(chatId, url, Collections.emptyList(), Collections.emptyList());

        when(restTemplate.exchange(any(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(LinkResponse.class)))
                .thenReturn(ResponseEntity.ok(expected));

        LinkResponse result = client.untrackLink(chatId, url);

        assertNotNull(result);
        assertEquals(expected.getLink(), result.getLink());
    }

    @Test
    void getLinks_shouldThrow_whenScrapperReturnsNull() {
        when(restTemplate.exchange(any(), eq(HttpMethod.GET), any(HttpEntity.class), eq(ListLinksResponse.class)))
                .thenReturn(ResponseEntity.ok(null));

        assertThrows(IllegalStateException.class, () -> client.getLinks(1L));
    }

    @Test
    void trackLink_shouldThrow_whenScrapperReturnsNull() {
        URI url = URI.create("https://example.com");
        when(restTemplate.postForEntity(any(), any(HttpEntity.class), eq(LinkResponse.class)))
                .thenReturn(ResponseEntity.ok(null));

        assertThrows(IllegalStateException.class, () -> client.trackLink(1L, url));
    }

    @Test
    void untrackLink_shouldThrow_whenScrapperReturnsNull() {
        URI url = URI.create("https://example.com");
        when(restTemplate.exchange(any(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(LinkResponse.class)))
                .thenReturn(ResponseEntity.ok(null));

        assertThrows(IllegalStateException.class, () -> client.untrackLink(1L, url));
    }
}
