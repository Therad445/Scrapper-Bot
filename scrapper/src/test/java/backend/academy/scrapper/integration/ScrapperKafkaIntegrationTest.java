package backend.academy.scrapper.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import backend.academy.scrapper.ScrapperApplication;
import backend.academy.scrapper.kafka.command.TrackCommand;
import backend.academy.scrapper.repository.LinkRepository;
import backend.academy.scrapper.repository.orm.LinkJpaRepository;
import java.net.URI;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(
        classes = ScrapperApplication.class,
        properties = {"app.message-transport=KAFKA"})
@ActiveProfiles("test")
public class ScrapperKafkaIntegrationTest {

    @Container
    private static final KafkaContainer kafkaContainer =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:8.1.3"));

    @DynamicPropertySource
    static void overrideKafkaBootstrapServers(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private LinkJpaRepository linkJpaRepository;

    @BeforeEach
    void cleanupDatabase() {
        linkJpaRepository.deleteAll();
    }

    @Test
    void whenValidTrackCommandSent_thenLinkIsPersisted() {
        TrackCommand cmd = new TrackCommand(123L, URI.create("https://example.org/page"), null, null);

        kafkaTemplate.send("bot-to-scrapper", cmd);

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> assertThat(linkRepository.findAllByChat(123L))
                .isNotEmpty()
                .allMatch(link -> link.url().toString().equals("https://example.org/page")));
    }
}
