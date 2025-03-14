package backend.academy.scrapper.dto;

import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

class LinkInfoTest {

    @Test
    void shouldCreateLinkInfoWithValidData() {
        // Arrange
        String expectedLink = "https://example.com";
        Set<String> expectedTags = Set.of("java", "backend");
        Set<String> expectedFilters = Set.of("filter1", "filter2");

        // Act
        LinkInfo linkInfo = new LinkInfo(expectedLink, expectedTags, expectedFilters);

        // Assert
        assertThat(linkInfo.getLink()).isEqualTo(expectedLink);
        assertThat(linkInfo.getTags()).containsExactlyInAnyOrderElementsOf(expectedTags);
        assertThat(linkInfo.getFilters()).containsExactlyInAnyOrderElementsOf(expectedFilters);
    }

    @Test
    void shouldHandleNullTagsAndFilters() {
        // Arrange
        String expectedLink = "https://example.com";

        // Act
        LinkInfo linkInfo = new LinkInfo(expectedLink, null, null);

        // Assert
        assertThat(linkInfo.getLink()).isEqualTo(expectedLink);
        assertThat(linkInfo.getTags()).isEmpty();
        assertThat(linkInfo.getFilters()).isEmpty();
    }

    @Test
    void shouldHandleEmptyTagsAndFilters() {
        // Arrange
        String expectedLink = "https://example.com";
        Set<String> emptyTags = Set.of();
        Set<String> emptyFilters = Set.of();

        // Act
        LinkInfo linkInfo = new LinkInfo(expectedLink, emptyTags, emptyFilters);

        // Assert
        assertThat(linkInfo.getLink()).isEqualTo(expectedLink);
        assertThat(linkInfo.getTags()).isEmpty();
        assertThat(linkInfo.getFilters()).isEmpty();
    }
}
