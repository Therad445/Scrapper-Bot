package backend.academy.bot.kafka;

import backend.academy.bot.dto.LinkUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;


@Component
@ConditionalOnProperty(prefix = "app.kafka", name = "topic")
public class ScrapperUpdatesListener {

    private static final Logger log = LoggerFactory.getLogger(ScrapperUpdatesListener.class);

    private final RestTemplate restTemplate;

    @Value("${app.scrapper-url}")
    private String scrapperBaseUrl;

    @Value("${app.kafka.topic}")
    private String kafkaTopic;

    public ScrapperUpdatesListener(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @KafkaListener(
        topics = "${app.kafka.topic}",
        groupId = "bot-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void onLinkUpdate(LinkUpdate update) {
        try {
            URI uri = URI.create(scrapperBaseUrl + "/updates");
            log.info("Получен LinkUpdate из Kafka (topic={}): {}, отправляем на {}", kafkaTopic, update, uri);

            restTemplate.postForEntity(uri, update, Void.class);
        } catch (RestClientException ex) {
            log.error("Ошибка при отправке LinkUpdate в BotController: {}", ex.getMessage(), ex);
        }
    }
}
