package backend.academy.scrapper.service.impl;

import backend.academy.scrapper.model.LinkInfo;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GitHubCheckerTest {

    private GitHubChecker checker;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setup() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        checker = new GitHubChecker(restTemplate);
        Field field = GitHubChecker.class.getDeclaredField("token");
        field.setAccessible(true);
        field.set(checker, "");
    }

    @Test
    @DisplayName("supports() — true for GitHub issue URL")
    void testSupports_shouldMatchValidGithubUrl() {
        var link = new LinkInfo(1L, "https://github.com/user/repo/issues/123", null, null, Set.of(), Set.of());
        assertTrue(checker.supports(link));
    }

    @Test
    @DisplayName("supports() — false for non-GitHub or invalid URL")
    void testSupports_shouldRejectNonGithubUrl() {
        var link = new LinkInfo(2L, "https://stackoverflow.com/q/123", null, null, Set.of(), Set.of());
        assertFalse(checker.supports(link));
    }

    @Test
    @DisplayName("hasUpdates() — returns true when remote issue is newer")
    void testHasUpdates_returnsTrueIfUpdated() {
        var url = "https://github.com/user/repo/issues/1";
        var link = new LinkInfo(1L, url, Instant.parse("2020-01-01T00:00:00Z"), null, Set.of(), Set.of());

        String githubResponse = """
            {
              "title": "Test issue",
              "user": { "login": "octocat" },
              "created_at": "2020-01-01T00:00:00Z",
              "updated_at": "2024-01-01T12:00:00Z",
              "body": "This is a test issue body"
            }
            """;

        mockServer.expect(requestTo("https://api.github.com/repos/user/repo/issues/1"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(githubResponse, MediaType.APPLICATION_JSON));

        assertTrue(checker.hasUpdates(link));
        assertEquals("GitHub: Test issue", checker.preview().split("\n")[0].strip()); // first line
        assertEquals(Instant.parse("2024-01-01T12:00:00Z"), checker.remoteUpdatedAt());

        mockServer.verify();
    }

    @Test
    void testHasUpdates_returnsFalseIfNotUpdated() throws Exception {
        String url = "https://github.com/owner/repo/issues/123";
        var link = new LinkInfo(
            1L,
            url,
            null,
            Instant.parse("2024-05-01T10:00:00Z"),
            Set.of(),
            Set.of()
        );

        String json = """
            {
              "title": "Issue title",
              "user": { "login": "user123" },
              "created_at": "2024-04-30T12:00:00Z",
              "updated_at": "2024-04-30T12:00:00Z",
              "body": "Some body text"
            }
            """;

        mockServer.expect(requestTo("https://api.github.com/repos/owner/repo/issues/123"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        boolean hasUpdates = checker.hasUpdates(link);

        assertFalse(hasUpdates);
        mockServer.verify();
    }


    @Test
    @DisplayName("hasUpdates() — returns false on invalid body or API error")
    void testHasUpdates_handlesNullResponseGracefully() {
        var url = "https://github.com/user/repo/issues/1";
        var link = new LinkInfo(1L, url, null, null, Set.of(), Set.of());

        mockServer.expect(requestTo("https://api.github.com/repos/user/repo/issues/1"))
            .andRespond(withNoContent());

        assertFalse(checker.hasUpdates(link));
        mockServer.verify();
    }
}
