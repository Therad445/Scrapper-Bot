package backend.academy.bot.state;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BotStateTest {

    @Test
    void testBotStateEnumValues() {
        // Arrange

        // Act
        BotState[] states = BotState.values();

        // Assert
        assertNotNull(states);
        assertEquals(3, states.length);
        assertEquals(BotState.NONE, states[0]);
        assertEquals(BotState.WAITING_FOR_TAGS, states[1]);
        assertEquals(BotState.WAITING_FOR_FILTERS, states[2]);
    }

    @Test
    void testBotStateValueOf() {
        // Arrange
        String stateString = "WAITING_FOR_TAGS";

        // Act
        BotState state = BotState.valueOf(stateString);

        // Assert
        assertEquals(BotState.WAITING_FOR_TAGS, state);
    }

    @Test
    void testBotStateValueOfInvalid() {
        // Arrange
        String invalidStateString = "INVALID_STATE";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> BotState.valueOf(invalidStateString));
    }
}
