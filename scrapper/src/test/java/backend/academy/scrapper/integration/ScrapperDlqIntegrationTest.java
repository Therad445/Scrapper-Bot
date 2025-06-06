package backend.academy.scrapper.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import backend.academy.scrapper.ScrapperApplication;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(
    classes = ScrapperApplication.class,
    properties = {
        "app.message-transport=kafka",
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "app.kafka.dlq-topic=scrapper-dlq"
    }
)
@ActiveProfiles("test")
public class ScrapperDlqIntegrationTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static KafkaConsumer<String, String> consumer;

    @BeforeAll
    static void setupConsumer() {
        Map<String, Object> props = Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getProperty("spring.kafka.bootstrap-servers"),
            ConsumerConfig.GROUP_ID_CONFIG, "scrapper-dlq-test-group",
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class
        );
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("scrapper-dlq"));
    }

    @AfterAll
    static void closeConsumer() {
        if (consumer != null) {
            consumer.close();
        }
    }

    @Test
    void whenInvalidJsonMessageSent_thenItGoesToDlq() {
        String badJson = "{\"chatId\":\"not-a-number\",\"url\":\"http://foo\"}";

        kafkaTemplate.send("bot-to-scrapper", badJson);

        boolean found = false;
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < Duration.ofSeconds(5).toMillis()) {
            for (ConsumerRecord<String, String> record : consumer.poll(Duration.ofMillis(200))) {
                if (record.value().contains("\"not-a-number\"")) {
                    found = true;
                    break;
                }
            }
            if (found) break;
        }

        assertThat(found)
            .as("Ожидали, что невалидный JSON будет перенаправлен в DLQ («scrapper-dlq»)")
            .isTrue();
    }

    @Test
    void whenValidationFails_thenMessageGoesToDlq() {
        String invalidForValidation = "{\"url\":\"http://foo.bar\"}";

        kafkaTemplate.send("bot-to-scrapper", invalidForValidation);

        boolean found = false;
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < Duration.ofSeconds(5).toMillis()) {
            for (ConsumerRecord<String, String> record : consumer.poll(Duration.ofMillis(200))) {
                if (record.value().contains("\"http://foo.bar\"")) {
                    found = true;
                    break;
                }
            }
            if (found) break;
        }

        assertThat(found)
            .as("Ожидали, что сообщение с отсутствующим chatId попадёт в DLQ")
            .isTrue();
    }
}
