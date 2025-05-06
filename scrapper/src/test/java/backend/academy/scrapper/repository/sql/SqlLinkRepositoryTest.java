package backend.academy.scrapper.repository.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import backend.academy.scrapper.model.LinkInfo;
import backend.academy.scrapper.repository.ChatRepository;
import backend.academy.scrapper.repository.LinkRepository;
import java.nio.file.Paths;
import java.sql.Connection;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.sql.DataSource;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.DirectoryResourceAccessor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SqlLinkRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("scrapper")
            .withUsername("user")
            .withPassword("pass");

    JdbcTemplate jdbc;
    LinkRepository repo;
    ChatRepository chatRepo;

    @BeforeAll
    void setupSchema() throws Exception {
        DataSource ds = DataSourceBuilder.create()
                .url(postgres.getJdbcUrl())
                .username(postgres.getUsername())
                .password(postgres.getPassword())
                .build();

        jdbc = new JdbcTemplate(ds);
        repo = new SqlLinkRepository(jdbc);
        chatRepo = new SqlChatRepository(jdbc);

        try (Connection conn = ds.getConnection()) {
            var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(conn));
            var resourceAccessor = new DirectoryResourceAccessor(
                    Paths.get("../migrations").toAbsolutePath().normalize());
            var liquibase = new Liquibase("master.xml", resourceAccessor, database);
            liquibase.update();
        }
    }

    @BeforeEach
    void cleanup() {
        jdbc.execute(
                "TRUNCATE tag, filter, link, subscription, subscription_tag, subscription_filter, chat RESTART IDENTITY CASCADE");
        chatRepo.register(1L);
        chatRepo.register(2L);
    }

    @Test
    void addAndFindAllByChat_shouldStoreAndReturnLink() {
        repo.add(1L, "https://example.com", Set.of("java"), Set.of("user:test"));

        List<LinkInfo> links = repo.findAllByChat(1L);

        assertEquals(1, links.size());
        assertEquals("https://example.com", links.getFirst().url());
    }

    @Test
    void remove_shouldDeleteSubscriptionOnly() {
        repo.add(1L, "https://remove.me", Set.of(), Set.of());

        Optional<LinkInfo> removed = repo.remove(1L, "https://remove.me");

        assertTrue(removed.isPresent());
        assertEquals("https://remove.me", removed.get().url());

        List<LinkInfo> links = repo.findAllByChat(1L);
        assertTrue(links.isEmpty());
    }

    @Test
    void updateCheckTime_shouldChangeFields() {
        repo.add(1L, "https://check.com", Set.of(), Set.of());
        long linkId = jdbc.queryForObject("SELECT id FROM link WHERE url = ?", Long.class, "https://check.com");

        Instant now = Instant.now();
        repo.updateCheckTime(linkId, now, now);

        var timestamps = jdbc.queryForMap("SELECT last_checked_at, last_updated_at FROM link WHERE id = ?", linkId);
        assertNotNull(timestamps.get("last_checked_at"));
        assertEquals(
                now.getEpochSecond(),
                ((java.sql.Timestamp) timestamps.get("last_checked_at"))
                        .toInstant()
                        .getEpochSecond());
    }

    @Test
    void findLinksForCheck_shouldReturnOldLinks() {
        Instant recent = Instant.now();

        repo.add(1L, "https://old.com", Set.of(), Set.of());
        repo.add(2L, "https://new.com", Set.of(), Set.of());

        long idOld = jdbc.queryForObject("SELECT id FROM link WHERE url = ?", Long.class, "https://old.com");
        long idNew = jdbc.queryForObject("SELECT id FROM link WHERE url = ?", Long.class, "https://new.com");

        repo.updateCheckTime(idOld, Instant.EPOCH, null);
        repo.updateCheckTime(idNew, Instant.now(), null);

        var page =
                repo.findLinksForCheck(recent.minusSeconds(1), org.springframework.data.domain.PageRequest.of(0, 10));

        assertEquals(1, page.getContent().size());
        assertEquals("https://old.com", page.getContent().getFirst().url());
    }

    @Test
    void addTagAndRemoveTag_shouldWorkCorrectly() {
        repo.add(1L, "https://tagged.com", Set.of(), Set.of());
        long linkId = jdbc.queryForObject("SELECT id FROM link WHERE url = ?", Long.class, "https://tagged.com");

        repo.addTag(1L, linkId, "tag123");
        int count = jdbc.queryForObject("SELECT count(*) FROM subscription_tag", Integer.class);
        assertEquals(1, count);

        repo.removeTag(1L, linkId, "tag123");
        count = jdbc.queryForObject("SELECT count(*) FROM subscription_tag", Integer.class);
        assertEquals(0, count);
    }

    @Test
    void addFilterAndRemoveFilter_shouldWorkCorrectly() {
        repo.add(1L, "https://filtered.com", Set.of(), Set.of());
        long linkId = jdbc.queryForObject("SELECT id FROM link WHERE url = ?", Long.class, "https://filtered.com");

        repo.addFilter(1L, linkId, "user:abc");
        int count = jdbc.queryForObject("SELECT count(*) FROM subscription_filter", Integer.class);
        assertEquals(1, count);

        repo.removeFilter(1L, linkId, "user:abc");
        count = jdbc.queryForObject("SELECT count(*) FROM subscription_filter", Integer.class);
        assertEquals(0, count);
    }
}
