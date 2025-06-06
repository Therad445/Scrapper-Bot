package backend.academy.scrapper.repository.orm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import backend.academy.scrapper.entity.ChatEntity;
import backend.academy.scrapper.entity.LinkEntity;
import backend.academy.scrapper.entity.SubscriptionEntity;
import backend.academy.scrapper.repository.ChatRepository;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrmChatRepositoryTest {

    @ServiceConnection
    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("scrapper")
            .withUsername("postgres")
            .withPassword("postgres");

    @Autowired
    ChatJpaRepository chatJpa;

    @Autowired
    LinkJpaRepository linkJpa;

    private ChatRepository ormChatRepository;

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add(
                "spring.liquibase.change-log",
                () -> "file:"
                        + Path.of("../migrations/master.xml")
                                .toAbsolutePath()
                                .normalize()
                                .toString());
        registry.add("spring.liquibase.enabled", () -> "true");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");

        registry.add("app.bot-url", () -> "http://localhost");
    }

    @BeforeEach
    void init() {
        ormChatRepository = new OrmChatRepository(chatJpa);
    }

    @Test
    void register_shouldCreateChat() {
        long chatId = 1001L;

        ormChatRepository.register(chatId);

        assertTrue(chatJpa.existsById(chatId));
        ChatEntity saved = chatJpa.findById(chatId).orElseThrow();
        assertEquals(chatId, saved.id());
        assertNotNull(saved.createdAt());
    }

    @Test
    void register_shouldNotDuplicateIfExists() {
        long chatId = 1002L;
        chatJpa.save(new ChatEntity(chatId, Instant.now()));

        ormChatRepository.register(chatId);

        assertEquals(1, chatJpa.count());
    }

    @Test
    void delete_shouldRemoveChat() {
        long chatId = 2001L;
        chatJpa.save(new ChatEntity(chatId, Instant.now()));

        ormChatRepository.delete(chatId);

        assertFalse(chatJpa.existsById(chatId));
    }

    @Test
    void exists_shouldReturnCorrectValue() {
        long existingId = 3001L;
        chatJpa.save(new ChatEntity(existingId, Instant.now()));

        assertTrue(ormChatRepository.exists(existingId));
        assertFalse(ormChatRepository.exists(existingId + 1));
    }

    @Test
    void findChatIdsByLinkId_shouldReturnCorrectIds() {
        var chat1 = new ChatEntity(1L, Instant.now());
        var chat2 = new ChatEntity(2L, Instant.now());
        chatJpa.saveAll(List.of(chat1, chat2));

        var link = new LinkEntity();
        link.url("https://test.com");
        link.lastCheckedAt(Instant.now());
        linkJpa.save(link);

        var sub1 = new SubscriptionEntity();
        sub1.chat(chat1);
        sub1.link(link);
        chat1.subscriptions().add(sub1);

        var sub2 = new SubscriptionEntity();
        sub2.chat(chat2);
        sub2.link(link);
        chat2.subscriptions().add(sub2);

        chatJpa.saveAll(List.of(chat1, chat2));

        var ids = ormChatRepository.findChatIdsByLinkId(link.id());

        assertEquals(2, ids.size());
        assertTrue(ids.containsAll(List.of(1L, 2L)));
    }
}
