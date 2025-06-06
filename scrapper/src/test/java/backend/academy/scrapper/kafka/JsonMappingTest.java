package backend.academy.scrapper.kafka;

import backend.academy.scrapper.kafka.command.ListCommand;
import backend.academy.scrapper.kafka.command.TrackCommand;
import backend.academy.scrapper.kafka.command.UntrackCommand;
import backend.academy.scrapper.model.LinkUpdate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JsonMappingTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void trackCommand_deserializesFromJson() throws Exception {
        String json = """
            {
              "chatId": 123,
              "link": "https://example.com/foo",
              "tags": ["tag1", "tag2"],
              "filters": ["user:alice"]
            }
            """;

        TrackCommand cmd = mapper.readValue(json, TrackCommand.class);
        assertEquals(123L, cmd.getChatId());
        assertEquals(URI.create("https://example.com/foo"), cmd.getLink());
        assertEquals(List.of("tag1", "tag2"), cmd.getTags());
        assertEquals(List.of("user:alice"), cmd.getFilters());
    }

    @Test
    void untrackCommand_deserializesFromJson() throws Exception {
        String json = """
            {
              "chatId": 456,
              "link": "https://example.com/bar"
            }
            """;

        UntrackCommand cmd = mapper.readValue(json, UntrackCommand.class);
        assertEquals(456L, cmd.getChatId());
        assertEquals(URI.create("https://example.com/bar"), cmd.getLink());
    }

    @Test
    void listCommand_deserializesFromJson() throws Exception {
        String json = """
            {
              "chatId": 789
            }
            """;

        ListCommand cmd = mapper.readValue(json, ListCommand.class);
        assertEquals(789L, cmd.getChatId());
    }

    @Test
    void linkUpdate_deserializesFromJson() throws Exception {
        String json = """
            {
              "id": 42,
              "url": "https://example.org/item",
              "description": "New update available",
              "tgChatIds": [1001, 1002, 1003]
            }
            """;

        LinkUpdate upd = mapper.readValue(json, LinkUpdate.class);
        assertEquals(42L, upd.id());
        assertEquals("https://example.org/item", upd.url());
        assertEquals("New update available", upd.description());
        assertEquals(Set.of(1001L, 1002L, 1003L), upd.tgChatIds());
    }

    @Test
    void roundTrip_trackCommand_serializationAndDeserialization() throws Exception {
        TrackCommand original = new TrackCommand(
            321L,
            new URI("https://foo.bar/baz"),
            List.of("x", "y"),
            List.of("filter1")
        );
        String json = mapper.writeValueAsString(original);
        TrackCommand parsed = mapper.readValue(json, TrackCommand.class);

        assertEquals(original.getChatId(), parsed.getChatId());
        assertEquals(original.getLink(), parsed.getLink());
        assertEquals(original.getTags(), parsed.getTags());
        assertEquals(original.getFilters(), parsed.getFilters());
    }

    @Test
    void roundTrip_linkUpdate_serializationAndDeserialization() throws Exception {
        LinkUpdate original = new LinkUpdate(
            555L,
            "https://foo.test/qux",
            "Check this out",
            Set.of(200L, 300L)
        );
        String json = mapper.writeValueAsString(original);
        LinkUpdate parsed = mapper.readValue(json, LinkUpdate.class);

        assertEquals(original.id(), parsed.id());
        assertEquals(original.url(), parsed.url());
        assertEquals(original.description(), parsed.description());
        assertEquals(original.tgChatIds(), parsed.tgChatIds());
    }
}
