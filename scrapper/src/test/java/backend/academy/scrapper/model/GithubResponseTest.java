package backend.academy.scrapper.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class GithubResponseTest {

    // Arrange
    @Test
    void testSetUpdated_at() {
        GithubResponse githubResponse = new GithubResponse();
        String expected = "2025-03-10T10:00:00";

        // Act
        githubResponse.setUpdated_at(expected);

        // Assert
        assertEquals(expected, githubResponse.getUpdated_at());
    }

    // Arrange
    @Test
    void testGetUpdated_at_DefaultValue() {
        GithubResponse githubResponse = new GithubResponse();

        // Act
        String actual = githubResponse.getUpdated_at();

        // Assert
        assertNull(actual, "Default value of updated_at should be null.");
    }
}
