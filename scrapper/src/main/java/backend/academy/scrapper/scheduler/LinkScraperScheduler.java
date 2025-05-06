package backend.academy.scrapper.scheduler;

import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.client.BotClient;
import backend.academy.scrapper.model.LinkInfo;
import backend.academy.scrapper.model.LinkUpdate;
import backend.academy.scrapper.repository.ChatRepository;
import backend.academy.scrapper.repository.LinkRepository;
import backend.academy.scrapper.service.impl.GitHubStackChecker;
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

    private final ScrapperConfig scrapperConfig;
    private final LinkRepository linkRepository;
    private final ChatRepository chatRepository;
    private final GitHubStackChecker checker;
    private final BotClient botClient;

    @Scheduled(fixedDelayString = "${app.scheduler.interval}")
    public void run() {
        var scheduler = scrapperConfig.scheduler();
        Instant threshold = Instant.now().minus(scheduler.forceCheckDelay());

        List<LinkInfo> links = linkRepository
            .findLinksForCheck(threshold, PageRequest.of(0, scheduler.batchSize()))
            .getContent();

        for (LinkInfo link : links) {
            List<Long> chatIds = chatRepository.findChatIdsByLinkId(link.id());
            if (chatIds.isEmpty()) continue;

            boolean hasChanges = checker.hasUpdates(link);

            if (hasChanges) {
                botClient.notifyUpdate(new LinkUpdate(
                    link.id(),
                    link.url(),
                    checker.preview(),
                    Set.copyOf(chatIds)
                ));
            }

            linkRepository.updateCheckTime(
                link.id(),
                Instant.now(),
                hasChanges ? Instant.now() : null
            );
        }

        log.info("Scheduler закончил проверку {} ссылок", links.size());
    }
}
