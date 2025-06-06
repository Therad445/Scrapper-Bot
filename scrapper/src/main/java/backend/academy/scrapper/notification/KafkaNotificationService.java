package backend.academy.scrapper.notification;

import backend.academy.scrapper.config.ScrapperProperties;
import backend.academy.scrapper.model.LinkUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "app", name = "message-transport", havingValue = "KAFKA")
public class KafkaNotificationService implements NotificationService {

    private final KafkaTemplate<String, LinkUpdate> kafkaTemplate;
    private final ScrapperProperties properties;

    @Override
    public void notify(LinkUpdate update) {
        kafkaTemplate.send(properties.kafka().topic(), update);
        log.info("Отправлено в Kafka: id={}, url={}", update.id(), update.url());
    }
}
