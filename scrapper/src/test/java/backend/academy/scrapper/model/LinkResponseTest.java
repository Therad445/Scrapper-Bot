package backend.academy.scrapper.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.Test;

class LinkResponseTest {

    @Test
    void shouldCreateLinkResponseWithValidData() {
        // Arrange
        Set<String> tags = Set.of("java", "backend");
        Set<String> filters = Set.of("filter1", "filter2");

        // Act
        LinkResponse response = new LinkResponse(1L, "https://example.com", tags, filters);

        // Assert
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getLink()).isEqualTo("https://example.com");
        assertThat(response.getTags()).containsExactlyInAnyOrder("java", "backend");
        assertThat(response.getFilters()).containsExactlyInAnyOrder("filter1", "filter2");
    }

    @Test
    void shouldHandleEmptyTagsAndFilters() {
        // Arrange
        Set<String> emptyTags = Set.of();
        Set<String> emptyFilters = Set.of();

        // Act
        LinkResponse response = new LinkResponse(2L, "https://example.com", emptyTags, emptyFilters);

        // Assert
        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getLink()).isEqualTo("https://example.com");
        assertThat(response.getTags()).isEmpty();
        assertThat(response.getFilters()).isEmpty();
    }

    @Test
    void shouldHandleNullTagsAndFilters() {
        // Arrange

        // Act
        LinkResponse response = new LinkResponse(3L, "https://example.com", null, null);

        // Assert
        assertThat(response.getId()).isEqualTo(3L);
        assertThat(response.getLink()).isEqualTo("https://example.com");
        assertThat(response.getTags()).isNull();
        assertThat(response.getFilters()).isNull();
    }
}
