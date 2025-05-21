package backend.academy.scrapper.notification;

import backend.academy.scrapper.config.ScrapperConfig;
import backend.academy.scrapper.model.LinkUpdate;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "app", name = "notification.type", havingValue = "http", matchIfMissing = true)
public class HttpNotificationService implements NotificationService {

    private final RestTemplate restTemplate;
    private final ScrapperConfig scrapperConfig;

    @Override
    public void notify(LinkUpdate update) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<LinkUpdate> request = new HttpEntity<>(update, headers);

            URI botBaseUrl = scrapperConfig.botUrl();
            restTemplate.postForEntity(botBaseUrl + "/updates", request, Void.class);

            log.info("Отправлено уведомление о ссылке id={}, url={}", update.id(), update.url());
        } catch (Exception e) {
            log.error("Ошибка при отправке уведомления", e);
            throw new IllegalStateException("Не удалось отправить уведомление", e);
        }
    }
}
