package backend.academy.scrapper.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UpdateInfoTest {

    // Arrange
    @Test
    void testSetLastUpdated() {
        UpdateInfo updateInfo = new UpdateInfo();
        String expected = "2025-03-10T10:00:00";

        // Act
        updateInfo.setLastUpdated(expected);

        // Assert
        assertEquals(expected, updateInfo.getLastUpdated());
    }

    // Arrange
    @Test
    void testGetLastUpdated_DefaultValue() {
        UpdateInfo updateInfo = new UpdateInfo();

        // Act
        String actual = updateInfo.getLastUpdated();

        // Assert
        assertEquals("", actual, "Default value of lastUpdated should be an empty string.");
    }
}
