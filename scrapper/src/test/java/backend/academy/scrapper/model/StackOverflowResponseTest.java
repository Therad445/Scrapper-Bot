package backend.academy.scrapper.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class StackOverflowResponseTest {

    // Arrange
    @Test
    void testStackOverflowResponse_SetGetItems() {
        StackOverflowResponse stackOverflowResponse = new StackOverflowResponse();
        StackOverflowItem item1 = new StackOverflowItem();
        item1.setLast_activity_date(1617938023L);
        StackOverflowItem item2 = new StackOverflowItem();
        item2.setLast_activity_date(1617938033L);
        List<StackOverflowItem> expectedItems = Arrays.asList(item1, item2);

        // Act
        stackOverflowResponse.setItems(expectedItems);
        List<StackOverflowItem> actualItems = stackOverflowResponse.getItems();

        // Assert
        assertNotNull(actualItems, "The items list should not be null.");
        assertEquals(2, actualItems.size(), "The number of items should match.");
        assertEquals(expectedItems, actualItems, "The items list should match the expected value.");
    }

    // Arrange
    @Test
    void testStackOverflowResponse_DefaultConstructor() {
        StackOverflowResponse stackOverflowResponse = new StackOverflowResponse();

        // Act
        List<StackOverflowItem> items = stackOverflowResponse.getItems();

        // Assert
        assertNull(items, "The default value of items should be null.");
    }
}
