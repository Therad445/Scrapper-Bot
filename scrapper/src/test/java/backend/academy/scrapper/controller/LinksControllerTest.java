package backend.academy.scrapper.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import backend.academy.scrapper.model.AddLinkRequest;
import backend.academy.scrapper.model.LinkResponse;
import backend.academy.scrapper.model.ListLinksResponse;
import backend.academy.scrapper.model.RemoveLinkRequest;
import backend.academy.scrapper.service.LinkService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class LinksControllerTest {

    @Mock
    private LinkService linkService;

    @InjectMocks
    private LinksController linksController;

    private Long chatId;
    private AddLinkRequest addLinkRequest;
    private RemoveLinkRequest removeLinkRequest;
    private LinkResponse linkResponse;
    private ListLinksResponse listLinksResponse;

    @BeforeEach
    public void setUp() {
        chatId = 123L;

        // Создаем коллекции для AddLinkRequest и LinkResponse
        Set<String> tags = new HashSet<>();
        tags.add("tag1");

        addLinkRequest = new AddLinkRequest("http://example.com", tags, tags);
        removeLinkRequest = new RemoveLinkRequest("http://example.com");

        linkResponse = new LinkResponse(123L, "http://example.com", tags, tags);

        List<LinkResponse> links = List.of(linkResponse);
        listLinksResponse = new ListLinksResponse(links, links.size());
    }

    @Test
    public void testGetLinks() {
        // Arrange
        when(linkService.getLinks(chatId)).thenReturn(listLinksResponse);

        // Act
        ResponseEntity<?> response = linksController.getLinks(chatId);

        // Assert
        assertEquals(
                200,
                response.getStatusCode().value()); // Используем getStatusCode().value() вместо getStatusCodeValue()
        assertEquals(listLinksResponse, response.getBody());
        verify(linkService, times(1)).getLinks(chatId);
    }

    @Test
    public void testAddLink() {
        // Arrange
        when(linkService.addLink(chatId, addLinkRequest)).thenReturn(linkResponse);

        // Act
        ResponseEntity<?> response = linksController.addLink(chatId, addLinkRequest);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertEquals(linkResponse, response.getBody());
        verify(linkService, times(1)).addLink(chatId, addLinkRequest);
    }

    @Test
    public void testDeleteLink() {
        // Arrange
        when(linkService.removeLinks(chatId, removeLinkRequest)).thenReturn(linkResponse);

        // Act
        ResponseEntity<?> response = linksController.deleteLink(chatId, removeLinkRequest);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertEquals(linkResponse, response.getBody());
        verify(linkService, times(1)).removeLinks(chatId, removeLinkRequest);
    }
}
