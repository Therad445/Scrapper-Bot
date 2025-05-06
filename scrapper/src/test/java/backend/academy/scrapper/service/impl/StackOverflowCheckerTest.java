package backend.academy.scrapper.service.impl;

import backend.academy.scrapper.model.LinkInfo;
import java.time.Instant;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class StackOverflowCheckerTest {

    private StackOverflowChecker checker;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setup() {
        RestTemplate restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        checker = new StackOverflowChecker(restTemplate);
    }

    @Test
    void supports_shouldRecognizeValidUrls() {
        assertTrue(checker.supports(new LinkInfo(1L,
            "https://stackoverflow.com/a/12345", null, null, Set.of(), Set.of())));
        assertTrue(checker.supports(new LinkInfo(2L,
            "https://stackoverflow.com/questions/67890/foo#comment123_456", null, null, Set.of(), Set.of())));
        assertFalse(checker.supports(new LinkInfo(3L,
            "https://github.com/user/repo", null, null, Set.of(), Set.of())));
    }

    @Test
    void hasUpdates_shouldReturnTrueForNewAnswer() {
        var link = new LinkInfo(1L,
            "https://stackoverflow.com/a/12345",
            Instant.parse("2020-01-01T00:00:00Z"),  // lastUpdatedAt
            null,
            Set.of(), Set.of());

        String answerJson = """
            {
              "items":[{
                "question_id":999,
                "creation_date":1700000000,
                "body_markdown":"Test body",
                "owner":{"display_name":"User123"}
              }]
            }""";

        String questionJson = """
            {
              "items":[{
                "title":"Example Question"
              }]
            }""";

        mockServer.expect(requestTo("https://api.stackexchange.com/2.3/answers/12345?site=stackoverflow&filter=withbody"))
            .andExpect(method(GET))
            .andRespond(withSuccess(answerJson, MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo("https://api.stackexchange.com/2.3/questions/999?site=stackoverflow&filter=withbody"))
            .andExpect(method(GET))
            .andRespond(withSuccess(questionJson, MediaType.APPLICATION_JSON));

        assertTrue(checker.hasUpdates(link));
        assertTrue(checker.preview().contains("Stack Overflow: Example Question"));
        assertEquals(Instant.ofEpochSecond(1700000000), checker.remoteUpdatedAt());
    }

    @Test
    void hasUpdates_shouldHandleEmptyItems() {
        var link = new LinkInfo(1L,
            "https://stackoverflow.com/a/12345", null, null, Set.of(), Set.of());

        String emptyJson = """
            { "items": [] }
            """;

        mockServer.expect(requestTo("https://api.stackexchange.com/2.3/answers/12345?site=stackoverflow&filter=withbody"))
            .andExpect(method(GET))
            .andRespond(withSuccess(emptyJson, MediaType.APPLICATION_JSON));

        assertFalse(checker.hasUpdates(link));
    }
}
