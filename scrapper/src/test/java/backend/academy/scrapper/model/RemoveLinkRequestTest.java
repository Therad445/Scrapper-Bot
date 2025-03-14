package backend.academy.scrapper.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class RemoveLinkRequestTest {

    @Test
    void shouldCreateRemoveLinkRequestWithValidData() {
        // Arrange
        String expectedLink = "https://example.com";

        // Act
        RemoveLinkRequest request = new RemoveLinkRequest(expectedLink);

        // Assert
        assertThat(request.getLink()).isEqualTo(expectedLink);
    }

    @Test
    void shouldHandleNullLink() {
        // Arrange

        // Act
        RemoveLinkRequest request = new RemoveLinkRequest(null);

        // Assert
        assertThat(request.getLink()).isNull();
    }

    @Test
    void shouldHandleEmptyLink() {
        // Arrange
        String emptyLink = "";

        // Act
        RemoveLinkRequest request = new RemoveLinkRequest(emptyLink);

        // Assert
        assertThat(request.getLink()).isEqualTo(emptyLink);
    }
}
