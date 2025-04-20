package backend.academy.scrapper.service;

import static org.junit.jupiter.api.Assertions.*;

import backend.academy.scrapper.dto.LinkInfo;
import backend.academy.scrapper.model.AddLinkRequest;
import backend.academy.scrapper.model.LinkResponse;
import backend.academy.scrapper.model.ListLinksResponse;
import backend.academy.scrapper.model.RemoveLinkRequest;
import backend.academy.scrapper.repository.InMemoryLinkRepository;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LinkServiceTest {

    private InMemoryLinkRepository inMemoryLinkRepository;
    private LinkService linkService;

    @BeforeEach
    void setUp() {
        inMemoryLinkRepository = new InMemoryLinkRepository();
        linkService = new LinkService(inMemoryLinkRepository);
    }

    @Test
    void shouldReturnListLinksResponseWhenGetLinksMethodIsCalled() {
        // Arrange
        Long chatId = 123L;
        Set<String> tags = new HashSet<>();
        tags.add("tag1");
        Set<String> filters = new HashSet<>();
        filters.add("filter1");

        LinkInfo linkInfo = new LinkInfo("http://example.com", tags, filters);
        inMemoryLinkRepository.addLink(chatId, linkInfo);

        // Act
        ListLinksResponse response = linkService.getLinks(chatId);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getSize());
        assertEquals("http://example.com", response.getLinks().get(0).getLink());
        assertEquals(tags, response.getLinks().get(0).getTags());
        assertEquals(filters, response.getLinks().get(0).getFilters());
    }

    @Test
    void shouldAddLinkAndReturnLinkResponseWhenAddLinkMethodIsCalled() {
        // Arrange
        Long chatId = 123L;
        Set<String> tags = new HashSet<>();
        tags.add("tag1");
        Set<String> filters = new HashSet<>();
        filters.add("filter1");

        AddLinkRequest addLinkRequest = new AddLinkRequest("http://example.com", tags, filters);
        LinkInfo linkInfo = new LinkInfo("http://example.com", tags, filters);

        // Act
        LinkResponse response = linkService.addLink(chatId, addLinkRequest);

        // Assert
        assertNotNull(response);
        assertEquals(chatId, response.getId());
        assertEquals("http://example.com", response.getLink());
        assertEquals(tags, response.getTags());
        assertEquals(filters, response.getFilters());
    }

    @Test
    void shouldRemoveLinkAndReturnLinkResponseWhenRemoveLinksMethodIsCalled() {
        // Arrange
        Long chatId = 123L;
        Set<String> tags = new HashSet<>();
        tags.add("tag1");
        Set<String> filters = new HashSet<>();
        filters.add("filter1");

        LinkInfo linkInfo = new LinkInfo("http://example.com", tags, filters);
        inMemoryLinkRepository.addLink(chatId, linkInfo);
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest("http://example.com");

        // Act
        LinkResponse response = linkService.removeLinks(chatId, removeLinkRequest);

        // Assert
        assertNotNull(response);
        assertEquals(chatId, response.getId());
        assertEquals("http://example.com", response.getLink());
        assertEquals(tags, response.getTags());
        assertEquals(filters, response.getFilters());
    }

    @Test
    void shouldThrowExceptionWhenRemoveLinksLinkNotFound() {
        // Arrange
        Long chatId = 123L;
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest("http://nonexistent.com");

        // Act & Assert
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> linkService.removeLinks(chatId, removeLinkRequest));
        assertEquals("Ссылка не найдена!", exception.getMessage());
    }

    @Test
    void shouldLogWhenAddingLink() {
        // Arrange
        Long chatId = 123L;
        Set<String> tags = new HashSet<>();
        tags.add("tag1");
        Set<String> filters = new HashSet<>();
        filters.add("filter1");

        AddLinkRequest addLinkRequest = new AddLinkRequest("http://example.com", tags, filters);

        // Act
        linkService.addLink(chatId, addLinkRequest);

        // Assert
        assertEquals(1, inMemoryLinkRepository.getLinks(chatId).size());
        assertEquals(
                "http://example.com", inMemoryLinkRepository.getLinks(chatId).get(0).getLink());
    }

    @Test
    public void testDuplicateLinkNotAdded() {
        // Arrange
        Long chatId = 100L;
        AddLinkRequest request = new AddLinkRequest("https://example.com", Set.of("tag"), Set.of("filter"));

        // Act
        LinkResponse firstResponse = linkService.addLink(chatId, request);
        LinkResponse duplicateResponse = linkService.addLink(chatId, request);

        // Assert
        assertNotNull(firstResponse, "Первая ссылка должна быть добавлена");
        assertNull(duplicateResponse, "Дублирующая ссылка не должна добавляться");
        var links = inMemoryLinkRepository.getLinks(chatId);
        assertEquals(1, links.size());
    }
}
