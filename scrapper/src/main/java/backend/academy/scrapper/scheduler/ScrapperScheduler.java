package backend.academy.scrapper.scheduler;

import backend.academy.scrapper.model.LinkUpdate;
import backend.academy.scrapper.model.TrackedLink;
import backend.academy.scrapper.service.NotificationClient;
import backend.academy.scrapper.service.TrackedLinkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class ScrapperScheduler {
    private final NotificationClient notificationClient;
    private final TrackedLinkService trackedLinkService;
    // Демонстрационные Telegram ID
    private final List<Long> demoTgChatIds = Arrays.asList(123456789L, 987654321L);
    private long updateIdCounter = 1;

    public ScrapperScheduler(NotificationClient notificationClient, TrackedLinkService trackedLinkService) {
        this.notificationClient = notificationClient;
        this.trackedLinkService = trackedLinkService;
    }

    @Scheduled(fixedRate = 60000)
    public void checkForUpdates() {
        log.info("Scrapper проверяет обновления всех отслеживаемых ссылок...");
        List<TrackedLink> trackedLinks = trackedLinkService.getAllTrackedLinks();
        for (TrackedLink trackedLink : trackedLinks) {
            // Симуляция проверки обновлений для каждой ссылки (30% вероятность обновления)
            if (Math.random() > 0.7) {
                String newUpdateValue = "update-" + System.currentTimeMillis();
                trackedLink.lastUpdate(newUpdateValue);
                trackedLinkService.updateTrackedLink(trackedLink);

                // Формируем уведомление согласно схеме LinkUpdate
                LinkUpdate update = new LinkUpdate();
                update.id(updateIdCounter++);
                update.url(trackedLink.url());
                update.description("Обнаружено новое обновление: " + newUpdateValue);
                update.tgChatIds(demoTgChatIds);

                log.info("Обновление обнаружено для {}: {}", trackedLink.url(), update.description());
                notificationClient.sendUpdate(update);
            } else {
                log.info("Обновлений для {} не обнаружено.", trackedLink.url());
            }
        }
    }
}
