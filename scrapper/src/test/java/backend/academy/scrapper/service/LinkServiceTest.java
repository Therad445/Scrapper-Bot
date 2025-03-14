package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.LinkInfo;
import backend.academy.scrapper.model.LinkResponse;
import backend.academy.scrapper.model.ListLinksResponse;
import backend.academy.scrapper.model.RemoveLinkRequest;
import backend.academy.scrapper.repository.LinkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LinkServiceTest {

    @Mock
    private LinkRepository linkRepository;

    @InjectMocks
    private LinkService linkService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
        when(linkRepository.get(chatId)).thenReturn(List.of(linkInfo));

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
    void shouldRemoveLinkAndReturnLinkResponseWhenRemoveLinksMethodIsCalled() {
        // Arrange
        Long chatId = 123L;
        Set<String> tags = new HashSet<>();
        tags.add("tag1");
        Set<String> filters = new HashSet<>();
        filters.add("filter1");

        LinkInfo linkInfo = new LinkInfo("http://example.com", tags, filters);
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest("http://example.com");

        when(linkRepository.remove(chatId, "http://example.com")).thenReturn(Optional.of(linkInfo));

        // Act
        LinkResponse response = linkService.removeLinks(chatId, removeLinkRequest);

        // Assert
        assertNotNull(response);
        assertEquals(chatId, response.getId());
        assertEquals("http://example.com", response.getLink());
        assertEquals(tags, response.getTags());
        assertEquals(filters, response.getFilters());

        // Verify that linkRepository.remove() was called once
        verify(linkRepository, times(1)).remove(chatId, "http://example.com");
    }

    @Test
    void shouldThrowExceptionWhenRemoveLinksLinkNotFound() {
        // Arrange
        Long chatId = 123L;
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest("http://nonexistent.com");

        when(linkRepository.remove(chatId, "http://nonexistent.com")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            linkService.removeLinks(chatId, removeLinkRequest)
        );
        assertEquals("Ссылка не найдена!", exception.getMessage());

        // Verify that linkRepository.remove() was called once
        verify(linkRepository, times(1)).remove(chatId, "http://nonexistent.com");
    }
}
