package backend.academy.scrapper.scheduler;

import backend.academy.scrapper.config.ScrapperProperties;
import backend.academy.scrapper.model.LinkInfo;
import backend.academy.scrapper.model.LinkUpdate;
import backend.academy.scrapper.notification.NotificationService;
import backend.academy.scrapper.repository.ChatRepository;
import backend.academy.scrapper.repository.LinkRepository;
import backend.academy.scrapper.service.LinkChecker;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
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

    private final ScrapperProperties scrapperProperties;
    private final LinkRepository linkRepo;
    private final ChatRepository chatRepo;
    private final List<LinkChecker> checkers;
    private final NotificationService notifier;
    private final ExecutorService linkCheckerPool; // ← инжектируем

    @Scheduled(fixedDelayString = "${app.scheduler.interval}")
    public void run() {
        var sch = scrapperProperties.scheduler();
        Instant threshold = Instant.now().minus(sch.forceCheckDelay());

        List<LinkInfo> batch = linkRepo.findLinksForCheck(threshold, PageRequest.of(0, sch.batchSize()))
                .getContent();
        if (batch.isEmpty()) return;

        int chunk = (int) Math.ceil((double) batch.size() / sch.threadCount());
        List<List<LinkInfo>> partitions = new ArrayList<>();
        for (int i = 0; i < batch.size(); i += chunk)
            partitions.add(batch.subList(i, Math.min(i + chunk, batch.size())));

        CountDownLatch latch = new CountDownLatch(partitions.size());

        for (List<LinkInfo> part : partitions) {
            linkCheckerPool.submit(() -> {
                try {
                    part.forEach(this::processSingle);
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("Scheduler проверил {} ссылок в {} поток(ах)", batch.size(), sch.threadCount());
    }

    private void processSingle(LinkInfo link) {
        List<Long> chatIds = chatRepo.findChatIdsByLinkId(link.id());

        checkers.stream().filter(ch -> ch.supports(link)).findFirst().ifPresent(ch -> {
            if (ch.hasUpdates(link) && !chatIds.isEmpty()) {
                notifier.notify(new LinkUpdate(link.id(), link.url(), ch.preview(), Set.copyOf(chatIds)));
            }
            linkRepo.updateCheckTime(link.id(), Instant.now(), ch.remoteUpdatedAt());
        });
    }
}
