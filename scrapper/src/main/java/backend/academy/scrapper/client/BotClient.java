package backend.academy.scrapper.client;

import backend.academy.scrapper.model.LinkUpdate;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class BotClient {

    private final RestTemplate restTemplate;

    @Autowired
    public BotClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void notifyUpdate(LinkUpdate update) {
        try {
            String botBaseUrl = "http://bot:8080";
            restTemplate.postForEntity(
                URI.create(botBaseUrl + "/updates"),
                update,
                Void.class);
            log.info("Отправлено уведомление о id={} url={}", update.id(), update.url());
        } catch (Exception e) {
            log.error("Ошибка при уведомлении", e);
            throw new IllegalStateException("Уведомление не удалось", e);
        }
    }
}
