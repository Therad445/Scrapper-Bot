package backend.academy.bot.dto;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddLinkRequestTest {

    @Test
    void testDefaultConstructor() {
        // Arrange
        AddLinkRequest addLinkRequest = new AddLinkRequest();

        // Act
        String link = addLinkRequest.getLink();
        List<String> tags = addLinkRequest.getTags();
        List<String> filters = addLinkRequest.getFilters();

        // Assert
        assertNull(link);
        assertNotNull(tags);
        assertTrue(tags.isEmpty());
        assertNotNull(filters);
        assertTrue(filters.isEmpty());
    }

    @Test
    void testParameterizedConstructor() {
        // Arrange
        String link = "http://example.com";
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> filters = Arrays.asList("filter1", "filter2");

        // Act
        AddLinkRequest addLinkRequest = new AddLinkRequest(link, tags, filters);

        // Assert
        assertEquals(link, addLinkRequest.getLink());
        assertEquals(tags, addLinkRequest.getTags());
        assertEquals(filters, addLinkRequest.getFilters());
    }

    @Test
    void testSetters() {
        // Arrange
        AddLinkRequest addLinkRequest = new AddLinkRequest();
        String link = "http://example.com";
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> filters = Arrays.asList("filter1", "filter2");

        // Act
        addLinkRequest.setLink(link);
        addLinkRequest.setTags(tags);
        addLinkRequest.setFilters(filters);

        // Assert
        assertEquals(link, addLinkRequest.getLink());
        assertEquals(tags, addLinkRequest.getTags());
        assertEquals(filters, addLinkRequest.getFilters());
    }
}
