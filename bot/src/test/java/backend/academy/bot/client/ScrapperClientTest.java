package backend.academy.bot.client;

import backend.academy.bot.dto.AddLinkRequest;
import backend.academy.bot.dto.LinkResponse;
import backend.academy.bot.dto.ListLinksResponse;
import java.net.URI;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ScrapperClientTest {

    @Mock
    private RestTemplate restTemplate;

    private ScrapperClient scrapperClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        scrapperClient = new ScrapperClient(restTemplate);
    }

    @Test
    void registerUser_ShouldSendPostRequest() {
        // Arrange
        Long chatId = 100L;
        URI uri = URI.create("http://scrapper:8081/tg-chat/" + chatId);

        when(restTemplate.postForEntity(eq(uri), eq(null), eq(Void.class)))
            .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        // Act
        scrapperClient.registerUser(chatId);

        // Assert
        verify(restTemplate, times(1)).postForEntity(eq(uri), eq(null), eq(Void.class));
    }

    @Test
    void getLinks_ShouldReturnLinksResponse() {
        // Arrange
        Long chatId = 100L;
        ListLinksResponse expectedResponse = new ListLinksResponse();
        when(restTemplate.exchange(
            any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(ListLinksResponse.class)))
            .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        // Act
        ListLinksResponse response = scrapperClient.getLinks(chatId);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse, response);
    }

    @Test
    void trackLink_ShouldSendPostRequestAndReturnLinkResponse() {
        // Arrange
        Long chatId = 100L;
        String url = "https://example.com";
        AddLinkRequest requestBody = new AddLinkRequest();
        requestBody.setLink(url);
        requestBody.setTags(Collections.emptyList());
        requestBody.setFilters(Collections.emptyList());

        LinkResponse expectedResponse = new LinkResponse(chatId, url, Collections.emptyList(), Collections.emptyList());

        URI uri = URI.create("http://scrapper:8081/links");

        when(restTemplate.postForEntity(eq(uri), any(HttpEntity.class), eq(LinkResponse.class)))
            .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        // Act
        LinkResponse response = scrapperClient.trackLink(chatId, url);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse.getLink(), response.getLink());
        assertEquals(expectedResponse.getId(), response.getId());
    }

    @Test
    void untrackLink_ShouldSendDeleteRequest() {
        // Arrange
        Long chatId = 100L;
        String url = "https://example.com";
        URI uri = URI.create("http://scrapper:8081:8081/links");

        when(restTemplate.exchange(eq(uri), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(LinkResponse.class)))
            .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        // Act
        scrapperClient.untrackLink(chatId, url);

        // Assert
        verify(restTemplate, times(1))
            .exchange(eq(uri), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(LinkResponse.class));
    }
}
