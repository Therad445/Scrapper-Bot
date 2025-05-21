package backend.academy.scrapper.repository.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import backend.academy.scrapper.repository.ChatRepository;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.List;
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
class SqlChatRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("scrapper")
            .withUsername("user")
            .withPassword("pass");

    JdbcTemplate jdbc;
    ChatRepository repo;

    @BeforeAll
    void setup() throws Exception {
        DataSource ds = DataSourceBuilder.create()
                .url(postgres.getJdbcUrl())
                .username(postgres.getUsername())
                .password(postgres.getPassword())
                .build();

        jdbc = new JdbcTemplate(ds);
        repo = new SqlChatRepository(jdbc);

        try (Connection conn = ds.getConnection()) {
            var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(conn));
            var liquibase = new Liquibase(
                    "master.xml",
                    new DirectoryResourceAccessor(
                            Path.of("../migrations").toAbsolutePath().normalize()),
                    database);
            liquibase.update();
        }
    }

    @BeforeEach
    void clean() {
        jdbc.execute("TRUNCATE chat, link, subscription RESTART IDENTITY CASCADE");
    }

    @Test
    void register_shouldInsertChat() {
        long id = 1001L;
        repo.register(id);
        Boolean exists = jdbc.queryForObject("SELECT EXISTS (SELECT 1 FROM chat WHERE id=?)", Boolean.class, id);
        assertEquals(Boolean.TRUE, exists);
    }

    @Test
    void delete_shouldRemoveChat() {
        long id = 2002L;
        jdbc.update("INSERT INTO chat(id, created_at) VALUES (?, now())", id);
        repo.delete(id);
        Boolean exists = jdbc.queryForObject("SELECT EXISTS (SELECT 1 FROM chat WHERE id=?)", Boolean.class, id);
        assertNotEquals(Boolean.TRUE, exists);
    }

    @Test
    void exists_shouldReturnTrueOrFalseCorrectly() {
        long id = 3003L;
        assertFalse(repo.exists(id));
        jdbc.update("INSERT INTO chat(id, created_at) VALUES (?, now())", id);
        assertTrue(repo.exists(id));
    }

    @Test
    void findChatIdsByLinkId_shouldReturnIds() {
        long chatId1 = 1L;
        long chatId2 = 2L;
        long linkId = 10L;

        jdbc.update("INSERT INTO chat(id, created_at) VALUES (?, now()), (?, now())", chatId1, chatId2);
        jdbc.update("INSERT INTO link(id, url, last_checked_at) VALUES (?, ?, now())", linkId, "https://link.com");
        jdbc.update(
                "INSERT INTO subscription(chat_id, link_id) VALUES (?, ?), (?, ?)", chatId1, linkId, chatId2, linkId);

        List<Long> result = repo.findChatIdsByLinkId(linkId);
        assertEquals(2, result.size());
        assertTrue(result.contains(chatId1));
        assertTrue(result.contains(chatId2));
    }
}
