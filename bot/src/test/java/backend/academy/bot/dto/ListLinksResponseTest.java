package backend.academy.bot.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class ListLinksResponseTest {

    @Test
    void testDefaultConstructor() {
        // Arrange
        ListLinksResponse listLinksResponse = new ListLinksResponse();

        // Act
        List<LinkResponse> links = listLinksResponse.getLinks();
        int size = listLinksResponse.getSize();

        // Assert
        assertNotNull(links);
        assertTrue(links.isEmpty());
        assertEquals(0, size);
    }

    @Test
    void testParameterizedConstructor() {
        // Arrange
        LinkResponse link1 =
                new LinkResponse(1L, "http://example1.com", Arrays.asList("tag1"), Arrays.asList("filter1"));
        LinkResponse link2 =
                new LinkResponse(2L, "http://example2.com", Arrays.asList("tag2"), Arrays.asList("filter2"));
        List<LinkResponse> links = Arrays.asList(link1, link2);
        int size = 2;

        // Act
        ListLinksResponse listLinksResponse = new ListLinksResponse(links, size);

        // Assert
        assertEquals(links, listLinksResponse.getLinks());
        assertEquals(size, listLinksResponse.getSize());
    }

    @Test
    void testSetters() {
        // Arrange
        ListLinksResponse listLinksResponse = new ListLinksResponse();
        LinkResponse link1 =
                new LinkResponse(1L, "http://example1.com", Arrays.asList("tag1"), Arrays.asList("filter1"));
        LinkResponse link2 =
                new LinkResponse(2L, "http://example2.com", Arrays.asList("tag2"), Arrays.asList("filter2"));
        List<LinkResponse> links = Arrays.asList(link1, link2);
        int size = 2;

        // Act
        listLinksResponse.setLinks(links);
        listLinksResponse.setSize(size);

        // Assert
        assertEquals(links, listLinksResponse.getLinks());
        assertEquals(size, listLinksResponse.getSize());
    }
}
