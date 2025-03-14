package backend.academy.scrapper.model;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AddLinkRequestTest {

    @Test
    void shouldCreateAddLinkRequestWithValidData() {
        // Arrange
        Set<String> tags = Set.of("java", "backend");
        Set<String> filters = Set.of("filter1", "filter2");

        // Act
        AddLinkRequest request = new AddLinkRequest("https://example.com", tags, filters);

        // Assert
        assertThat(request.getLink()).isEqualTo("https://example.com");
        assertThat(request.getTags()).containsExactlyInAnyOrder("java", "backend");
        assertThat(request.getFilters()).containsExactlyInAnyOrder("filter1", "filter2");
    }

    @Test
    void shouldAllowEmptyTagsAndFilters() {
        // Arrange
        Set<String> emptyTags = Set.of();
        Set<String> emptyFilters = Set.of();

        // Act
        AddLinkRequest request = new AddLinkRequest("https://example.com", emptyTags, emptyFilters);

        // Assert
        assertThat(request.getLink()).isEqualTo("https://example.com");
        assertThat(request.getTags()).isEmpty();
        assertThat(request.getFilters()).isEmpty();
    }

    @Test
    void shouldRejectInvalidUrl() {
        // Arrange
        String invalidUrl = "invalid-url";

        // Act
        AddLinkRequest request = new AddLinkRequest(invalidUrl, Set.of(), Set.of());

        // Assert
        assertThat(request.getLink()).isEqualTo(invalidUrl);
    }
}
