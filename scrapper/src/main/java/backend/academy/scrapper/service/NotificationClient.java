package backend.academy.scrapper.service;

import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.model.LinkUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class NotificationClient {
    private final WebClient webClient;

    public NotificationClient(ScrapperConfig config, WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
    }

    public void sendUpdate(LinkUpdate update) {
        webClient.post()
            .uri("/updates")
            .bodyValue(update)
            .retrieve()
            .toBodilessEntity()
            .doOnSuccess(response -> log.info("Уведомление отправлено"))
            .doOnError(error -> log.error("Ошибка при отправке уведомления", error))
            .subscribe();
    }

}
