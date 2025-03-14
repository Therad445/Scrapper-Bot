package backend.academy.bot.state;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserSessionTest {

    private UserSession userSession;

    @BeforeEach
    void setUp() {
        userSession = new UserSession();
    }

    @Test
    void testGetStateInitial() {
        // Arrange

        // Act
        BotState state = userSession.getState();

        // Assert
        assertEquals(BotState.NONE, state);
    }

    @Test
    void testSetState() {
        // Arrange
        BotState expectedState = BotState.NONE;

        // Act
        userSession.setState(expectedState);
        BotState actualState = userSession.getState();

        // Assert
        assertEquals(expectedState, actualState);
    }

    @Test
    void testGetPendingUrlInitial() {
        // Arrange

        // Act
        String pendingUrl = userSession.getPendingUrl();

        // Assert
        assertNull(pendingUrl);
    }

    @Test
    void testSetPendingUrl() {
        // Arrange
        String expectedUrl = "http://example.com";

        // Act
        userSession.setPendingUrl(expectedUrl);
        String actualUrl = userSession.getPendingUrl();

        // Assert
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    void testGetPendingTagsInitial() {
        // Arrange

        // Act
        String pendingTags = userSession.getPendingTags();

        // Assert
        assertNull(pendingTags);
    }

    @Test
    void testSetPendingTags() {
        // Arrange
        String expectedTags = "tag1, tag2";

        // Act
        userSession.setPendingTags(expectedTags);
        String actualTags = userSession.getPendingTags();

        // Assert
        assertEquals(expectedTags, actualTags);
    }

    @Test
    void testGetPendingFiltersInitial() {
        // Arrange

        // Act
        String pendingFilters = userSession.getPendingFilters();

        // Assert
        assertNull(pendingFilters);
    }

    @Test
    void testSetPendingFilters() {
        // Arrange
        String expectedFilters = "filter1, filter2";

        // Act
        userSession.setPendingFilters(expectedFilters);
        String actualFilters = userSession.getPendingFilters();

        // Assert
        assertEquals(expectedFilters, actualFilters);
    }

    @Test
    void testReset() {
        // Arrange
        userSession.setState(BotState.NONE);
        userSession.setPendingUrl("http://example.com");
        userSession.setPendingTags("tag1, tag2");
        userSession.setPendingFilters("filter1, filter2");

        // Act
        userSession.reset();

        // Assert
        assertEquals(BotState.NONE, userSession.getState());
        assertNull(userSession.getPendingUrl());
        assertNull(userSession.getPendingTags());
        assertNull(userSession.getPendingFilters());
    }
}
