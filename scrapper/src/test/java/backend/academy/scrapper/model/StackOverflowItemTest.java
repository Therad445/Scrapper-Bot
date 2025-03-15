package backend.academy.scrapper.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StackOverflowItemTest {

    // Arrange
    @Test
    void testStackOverflowItem_SetGetLastActivityDate() {
        StackOverflowItem stackOverflowItem = new StackOverflowItem();
        long expectedDate = 1617938023L;

        // Act
        stackOverflowItem.setLast_activity_date(expectedDate);
        long actualDate = stackOverflowItem.getLast_activity_date();

        // Assert
        assertEquals(expectedDate, actualDate, "The last activity date should match the expected value.");
    }

    // Arrange
    @Test
    void testStackOverflowItem_DefaultConstructor() {
        StackOverflowItem stackOverflowItem = new StackOverflowItem();

        // Act
        long lastActivityDate = stackOverflowItem.getLast_activity_date();

        // Assert
        assertEquals(0L, lastActivityDate, "The default value of last_activity_date should be 0.");
    }
}
