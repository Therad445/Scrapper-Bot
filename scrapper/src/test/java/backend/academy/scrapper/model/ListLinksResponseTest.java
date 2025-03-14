package backend.academy.scrapper.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

class ListLinksResponseTest {

    @Test
    void shouldCreateListLinksResponseWithValidData() {
        // Arrange
        LinkResponse link1 = new LinkResponse(1L, "https://example1.com", Set.of("java"), Set.of("filter1"));
        LinkResponse link2 = new LinkResponse(2L, "https://example2.com", Set.of("backend"), Set.of("filter2"));
        List<LinkResponse> links = List.of(link1, link2);
        int expectedSize = links.size();

        // Act
        ListLinksResponse response = new ListLinksResponse(links, expectedSize);

        // Assert
        assertThat(response.getLinks()).containsExactly(link1, link2);
        assertThat(response.getSize()).isEqualTo(expectedSize);
    }

    @Test
    void shouldHandleEmptyList() {
        // Arrange
        List<LinkResponse> emptyLinks = List.of();
        int expectedSize = 0;

        // Act
        ListLinksResponse response = new ListLinksResponse(emptyLinks, expectedSize);

        // Assert
        assertThat(response.getLinks()).isEmpty();
        assertThat(response.getSize()).isEqualTo(expectedSize);
    }

    @Test
    void shouldHandleNullList() {
        // Arrange

        // Act
        ListLinksResponse response = new ListLinksResponse(null, 0);

        // Assert
        assertThat(response.getLinks()).isNull();
        assertThat(response.getSize()).isEqualTo(0);
    }
}
