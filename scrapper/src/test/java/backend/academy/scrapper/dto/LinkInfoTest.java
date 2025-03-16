package backend.academy.scrapper.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

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

    @Test
    void shouldBeEqualIfSameLinkTagsAndFilters() {
        // Arrange
        LinkInfo linkInfo1 = new LinkInfo("https://example.com", Set.of("tag1"), Set.of("filter1"));
        LinkInfo linkInfo2 = new LinkInfo("https://example.com", Set.of("tag1"), Set.of("filter1"));

        // Act & Assert
        assertThat(linkInfo1).isEqualTo(linkInfo2);
        assertThat(linkInfo1.hashCode()).isEqualTo(linkInfo2.hashCode());
    }

    @Test
    void shouldNotBeEqualIfDifferentLink() {
        // Arrange
        LinkInfo linkInfo1 = new LinkInfo("https://example1.com", Set.of("tag1"), Set.of("filter1"));
        LinkInfo linkInfo2 = new LinkInfo("https://example2.com", Set.of("tag1"), Set.of("filter1"));

        // Act & Assert
        assertThat(linkInfo1).isNotEqualTo(linkInfo2);
    }

    @Test
    void shouldHaveUpdateInfoAfterCreation() {
        // Arrange
        LinkInfo linkInfo = new LinkInfo("https://example.com", Set.of("tag1"), Set.of("filter1"));

        // Act & Assert
        assertThat(linkInfo.getUpdateInfo()).isNotNull();
    }

    @Test
    void shouldNotModifyOriginalTagsAndFilters() {
        // Arrange
        Set<String> tags = new HashSet<>();
        tags.add("tag1");
        Set<String> filters = new HashSet<>();
        filters.add("filter1");

        LinkInfo linkInfo = new LinkInfo("https://example.com", tags, filters);

        // Modify original sets
        tags.add("newTag");
        filters.add("newFilter");

        // Act & Assert
        assertThat(linkInfo.getTags()).doesNotContain("newTag");
        assertThat(linkInfo.getFilters()).doesNotContain("newFilter");
    }
}
