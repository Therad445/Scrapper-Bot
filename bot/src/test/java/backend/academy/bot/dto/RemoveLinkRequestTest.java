package backend.academy.bot.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RemoveLinkRequestTest {

    @Test
    void testDefaultConstructor() {
        // Arrange
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest();

        // Act
        String link = removeLinkRequest.getLink();

        // Assert
        assertNull(link);
    }

    @Test
    void testSetterGetter() {
        // Arrange
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest();
        String expectedLink = "http://example.com";

        // Act
        removeLinkRequest.setLink(expectedLink);
        String actualLink = removeLinkRequest.getLink();

        // Assert
        assertEquals(expectedLink, actualLink);
    }
}
