package backend.academy.scrapper.repository.orm;

import backend.academy.scrapper.entity.ChatEntity;
import backend.academy.scrapper.model.LinkInfo;
import jakarta.transaction.Transactional;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(OrmLinkRepository.class)
class OrmLinkRepositoryTest {

    @ServiceConnection
    @Container
    static final PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("scrapper")
            .withUsername("postgres")
            .withPassword("postgres");
    private final long chatId = 1L;
    @Autowired
    OrmLinkRepository ormLinkRepository;
    @Autowired
    ChatJpaRepository chatJpa;
    @Autowired
    LinkJpaRepository linkJpa;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry r) {
        r.add("spring.liquibase.change-log", () ->
            "file:" + Paths.get("../migrations/master.xml")
                .toAbsolutePath().normalize());
        r.add("spring.liquibase.enabled", () -> "true");
        r.add("spring.jpa.hibernate.ddl-auto", () -> "none");
    }

    @BeforeEach
    void setUp() {
        if (!chatJpa.existsById(chatId)) {
            var chat = new ChatEntity(chatId, Instant.now());
            chatJpa.save(chat);
        }
    }

    @Test
    @DisplayName("add() — должен сохранять новую ссылку и подписку")
    @Transactional
    void add_and_findAllByChat() {
        String url = "https://example.com";
        ormLinkRepository.add(chatId, url, Set.of("dev"), Set.of("user:bob"));

        List<LinkInfo> links = ormLinkRepository.findAllByChat(chatId);
        assertEquals(1, links.size());
        LinkInfo li = links.getFirst();
        assertEquals(url, li.url());
        assertEquals(Set.of("dev"), li.tags());
        assertEquals(Set.of("user:bob"), li.filters());
    }

    @Test
    @DisplayName("remove() — должен удалить только подписку")
    @Transactional
    void remove_subscription_only() {
        String url = "https://remove.me";
        ormLinkRepository.add(chatId, url, Set.of(), Set.of());

        Optional<LinkInfo> removed = ormLinkRepository.remove(chatId, url);

        assertTrue(removed.isPresent());
        assertEquals(url, removed.get().url());
        assertTrue(ormLinkRepository.findAllByChat(chatId).isEmpty());
    }

    @Test
    @DisplayName("updateCheckTime() — корректно обновляет временные метки")
    @Transactional
    void updateCheckTime() {
        String url = "https://check.com";
        ormLinkRepository.add(chatId, url, Set.of(), Set.of());

        long linkId = linkJpa.findByUrl(url).orElseThrow().id();
        Instant now = Instant.now();

        ormLinkRepository.updateCheckTime(linkId, now, now);

        var updated = linkJpa.findById(linkId).orElseThrow();
        assertEquals(now, updated.lastCheckedAt());
        assertEquals(now, updated.lastUpdatedAt());
    }

    @Test
    @DisplayName("addTag/removeTag — добавляет и удаляет тэги")
    @Transactional
    void add_and_remove_tag() {
        String url = "https://tagged.com";
        ormLinkRepository.add(chatId, url, Set.of(), Set.of());

        long linkId = linkJpa.findByUrl(url).orElseThrow().id();
        ormLinkRepository.addTag(chatId, linkId, "spring");

        assertEquals(Set.of("spring"),
            ormLinkRepository.findAllByChat(chatId).getFirst().tags());

        ormLinkRepository.removeTag(chatId, linkId, "spring");

        assertTrue(ormLinkRepository.findAllByChat(chatId).getFirst().tags().isEmpty());
    }

    @Test
    @DisplayName("addFilter/removeFilter — добавляет и удаляет фильтры")
    @Transactional
    void add_and_remove_filter() {
        String url = "https://filtered.com";
        ormLinkRepository.add(chatId, url, Set.of(), Set.of());

        long linkId = linkJpa.findByUrl(url).orElseThrow().id();
        ormLinkRepository.addFilter(chatId, linkId, "user:x");

        assertEquals(Set.of("user:x"),
            ormLinkRepository.findAllByChat(chatId).getFirst().filters());

        ormLinkRepository.removeFilter(chatId, linkId, "user:x");

        assertTrue(ormLinkRepository.findAllByChat(chatId).getFirst().filters().isEmpty());
    }

    @Test
    @DisplayName("findLinksForCheck() — возвращает только устаревшие ссылки")
    void findLinksForCheck() {
        ormLinkRepository.add(chatId, "https://old.com", Set.of(), Set.of());
        ormLinkRepository.add(chatId, "https://new.com", Set.of(), Set.of());

        var old = linkJpa.findByUrl("https://old.com").orElseThrow();
        var fresh = linkJpa.findByUrl("https://new.com").orElseThrow();

        old.lastCheckedAt(Instant.EPOCH);
        fresh.lastCheckedAt(Instant.now());
        linkJpa.saveAll(List.of(old, fresh));

        var page = ormLinkRepository.findLinksForCheck(
            Instant.now().minusSeconds(10),
            org.springframework.data.domain.PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
        assertEquals("https://old.com", page.getContent().getFirst().url());
    }

    @TestConfiguration
    static class Config {
        @Bean
        OrmLinkRepository ormLinkRepository(
            LinkJpaRepository linkJpa,
            ChatJpaRepository chatJpa,
            TagJpaRepository tagJpa,
            FilterJpaRepository filterJpa
        ) {
            return new OrmLinkRepository(linkJpa, chatJpa, tagJpa, filterJpa);
        }
    }
}
