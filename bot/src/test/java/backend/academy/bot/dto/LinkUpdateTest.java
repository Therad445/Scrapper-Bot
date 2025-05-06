package backend.academy.bot.dto;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LinkUpdateTest {

    @Test
    void testValidLinkUpdate() {
        // Arrange
        Set<Long> tgChatIds = new HashSet<>();
        tgChatIds.add(123L);
        LinkUpdate linkUpdate = new LinkUpdate(1L, "http://example.com", "Test description", tgChatIds);

        // Act

        // Assert
        assertEquals(1L, linkUpdate.getId());
        assertEquals("http://example.com", linkUpdate.getUrl());
        assertEquals("Test description", linkUpdate.getDescription());
        assertEquals(tgChatIds, linkUpdate.getTgChatIds());
    }

    @Test
    void testInvalidUrl() {
        // Arrange
        Set<Long> tgChatIds = new HashSet<>();
        tgChatIds.add(123L);
        LinkUpdate linkUpdate = new LinkUpdate(1L, "invalid-url", "Test description", tgChatIds);

        // Act

        // Assert
        assertEquals("invalid-url", linkUpdate.getUrl());
    }

    @Test
    void testEmptyUrl() {
        // Arrange
        Set<Long> tgChatIds = new HashSet<>();
        tgChatIds.add(123L);
        LinkUpdate linkUpdate = new LinkUpdate(1L, "", "Test description", tgChatIds);

        // Act & Assert
        assertEquals("", linkUpdate.getUrl());
    }

    @Test
    void testNullTgChatIds() {
        // Arrange
        LinkUpdate linkUpdate = new LinkUpdate(1L, "http://example.com", "Test description", null);

        // Act & Assert
        assertEquals(Collections.emptySet(), linkUpdate.getTgChatIds());
    }

    @Test
    void testEmptyTgChatIds() {
        // Arrange
        Set<Long> tgChatIds = new HashSet<>();
        LinkUpdate linkUpdate = new LinkUpdate(1L, "http://example.com", "Test description", tgChatIds);

        // Act & Assert
        assertTrue(linkUpdate.getTgChatIds().isEmpty());
    }

    @Test
    void testGetters() {
        // Arrange
        Set<Long> tgChatIds = new HashSet<>();
        tgChatIds.add(123L);
        LinkUpdate linkUpdate = new LinkUpdate(1L, "http://example.com", "Test description", tgChatIds);

        // Act & Assert
        assertEquals(1L, linkUpdate.getId());
        assertEquals("http://example.com", linkUpdate.getUrl());
        assertEquals("Test description", linkUpdate.getDescription());
        assertEquals(tgChatIds, linkUpdate.getTgChatIds());
    }
}
