package backend.academy.scrapper.scheduler;

import backend.academy.scrapper.config.ScrapperConfig;
import backend.academy.scrapper.model.LinkInfo;
import backend.academy.scrapper.model.LinkUpdate;
import backend.academy.scrapper.notification.NotificationService;
import backend.academy.scrapper.repository.ChatRepository;
import backend.academy.scrapper.repository.LinkRepository;
import backend.academy.scrapper.service.LinkChecker;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.scheduler.enable", havingValue = "true")
public class LinkScraperScheduler {
    private final ScrapperConfig config;
    private final LinkRepository linkRepository;
    private final ChatRepository chatRepository;
    private final List<LinkChecker> checkers;
    private final NotificationService notifier;

    @Scheduled(fixedDelayString = "${app.scheduler.interval}")
    public void run() {
        var sch = config.scheduler();
        Instant threshold = Instant.now().minus(sch.forceCheckDelay());

        List<LinkInfo> links = linkRepository
            .findLinksForCheck(threshold, PageRequest.of(0, sch.batchSize()))
            .getContent();

        for (LinkInfo link : links) {
            List<Long> chatIds = chatRepository.findChatIdsByLinkId(link.id());
            if (chatIds.isEmpty()) continue;

            checkers.stream()
                .filter(ch -> ch.supports(link))
                .findFirst()
                .ifPresent(ch -> {
                    if (ch.hasUpdates(link)) {
                        notifier.notify(new LinkUpdate(
                            link.id(), link.url(), ch.preview(), Set.copyOf(chatIds)));
                    }
                    linkRepository.updateCheckTime(
                        link.id(),
                        Instant.now(),
                        ch.remoteUpdatedAt());
                });
        }
        log.info("Scheduler закончил проверку {} ссылок", links.size());
    }
}
