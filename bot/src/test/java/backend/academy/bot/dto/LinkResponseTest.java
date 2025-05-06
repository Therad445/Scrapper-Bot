package backend.academy.bot.dto;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LinkResponseTest {

    @Test
    void testDefaultConstructor() {
        // Arrange
        LinkResponse linkResponse = new LinkResponse();

        // Act
        Long id = linkResponse.getId();
        String link = linkResponse.getLink();
        List<String> tags = linkResponse.getTags();
        List<String> filters = linkResponse.getFilters();

        // Assert
        assertNull(id);
        assertNull(link);
        assertNotNull(tags);
        assertTrue(tags.isEmpty());
        assertNotNull(filters);
        assertTrue(filters.isEmpty());
    }

    @Test
    void testParameterizedConstructor() {
        // Arrange
        Long id = 1L;
        String link = "http://example.com";
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> filters = Arrays.asList("filter1", "filter2");

        // Act
        LinkResponse linkResponse = new LinkResponse(id, link, tags, filters);

        // Assert
        assertEquals(id, linkResponse.getId());
        assertEquals(link, linkResponse.getLink());
        assertEquals(tags, linkResponse.getTags());
        assertEquals(filters, linkResponse.getFilters());
    }

    @Test
    void testSetters() {
        // Arrange
        Long id = 1L;
        String link = "http://example.com";
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> filters = Arrays.asList("filter1", "filter2");

        // Act
        LinkResponse response = new LinkResponse(id, link, tags, filters);

        // Assert
        assertEquals(id, response.getId());
        assertEquals(link, response.getLink());
        assertEquals(tags, response.getTags());
        assertEquals(filters, response.getFilters());
    }
}
