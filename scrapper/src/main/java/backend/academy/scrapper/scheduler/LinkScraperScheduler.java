package backend.academy.scrapper.scheduler;

import backend.academy.scrapper.client.BotClient;
import backend.academy.scrapper.dto.LinkInfo;
import backend.academy.scrapper.model.GithubResponse;
import backend.academy.scrapper.model.LinkUpdate;
import backend.academy.scrapper.model.StackOverflowItem;
import backend.academy.scrapper.model.StackOverflowResponse;
import backend.academy.scrapper.repository.LinkRepository;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class LinkScraperScheduler {

    private final LinkRepository linkRepository;
    private final BotClient botClient;
    private final RestTemplate restTemplate;

    private final ConcurrentMap<Long, List<LinkUpdate>> batchUpdates = new ConcurrentHashMap<>();

    public LinkScraperScheduler(LinkRepository linkRepository, BotClient botClient, RestTemplate restTemplate) {
        this.linkRepository = linkRepository;
        this.botClient = botClient;
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedDelay = 600000)
    public void checkForUpdates() {
        log.info("Проверка обновлений ссылок...");
        linkRepository.getAllLinks().forEach((chatId, linkSet) -> {
            for (LinkInfo linkInfo : linkSet) {
                String url = linkInfo.getLink();
                boolean isUpdated = false;
                if (url.contains("github.com")) {
                    isUpdated = checkGithubLink(linkInfo);
                } else if (url.contains("stackoverflow.com")) {
                    isUpdated = checkStackOverflowLink(linkInfo);
                }
                if (isUpdated) {
                    addBatchUpdate(chatId, linkInfo);
                }
            }
        });
    }

    private boolean checkGithubLink(LinkInfo linkInfo) {
        try {
            URI uri = new URI(linkInfo.getLink());
            String[] segments = uri.getPath().split("/");
            if (segments.length < 3) {
                return false;
            }
            String owner = segments[1];
            String repo = segments[2];
            String apiUrl = "https://api.github.com/repos/" + owner + "/" + repo;
            GithubResponse response = restTemplate.getForObject(apiUrl, GithubResponse.class);
            if (response != null) {
                String newUpdated = response.getUpdated_at();
                log.info(
                        "GitHub обновление для {}: старое {}, новое {}",
                        linkInfo.getLink(),
                        linkInfo.getUpdateInfo().getLastUpdated(),
                        newUpdated);
                if (!newUpdated.equals(linkInfo.getUpdateInfo().getLastUpdated())) {
                    linkInfo.getUpdateInfo().setLastUpdated(newUpdated);
                    log.info("GitHub обновление обнаружено для: {}", linkInfo.getLink());
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("Ошибка при проверке GitHub ссылки: {}", linkInfo.getLink(), e);
        }
        return false;
    }

    private boolean checkStackOverflowLink(LinkInfo linkInfo) {
        try {
            // Пример URL: https://stackoverflow.com/questions/1234567/...
            URI uri = new URI(linkInfo.getLink());
            String[] segments = uri.getPath().split("/");
            int index = -1;
            for (int i = 0; i < segments.length; i++) {
                if ("questions".equals(segments[i])) {
                    index = i;
                    break;
                }
            }
            if (index == -1 || segments.length <= index + 1) {
                return false;
            }
            String questionId = segments[index + 1];
            String apiUrl = "https://api.stackexchange.com/2.3/questions/" + questionId
                    + "?order=desc&sort=activity&site=stackoverflow";
            StackOverflowResponse response = restTemplate.getForObject(apiUrl, StackOverflowResponse.class);
            if (response != null
                    && response.getItems() != null
                    && !response.getItems().isEmpty()) {
                StackOverflowItem item = response.getItems().get(0);
                String newActivity = String.valueOf(item.getLast_activity_date());
                if (!newActivity.equals(linkInfo.getUpdateInfo().getLastUpdated())) {
                    linkInfo.getUpdateInfo().setLastUpdated(newActivity);
                    log.info("StackOverflow обновление обнаружено для: {}", linkInfo.getLink());
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("Ошибка при проверке StackOverflow ссылки: {}", linkInfo.getLink(), e);
        }
        return false;
    }

    private void addBatchUpdate(Long chatId, LinkInfo linkInfo) {
        LinkUpdate update = new LinkUpdate(0L, linkInfo.getLink(), "Обнаружено обновление", Set.of(chatId));
        batchUpdates.compute(chatId, (id, updates) -> {
            if (updates == null) {
                updates = new ArrayList<>();
            }
            updates.add(update);
            return updates;
        });
    }

    @Scheduled(cron = "0 0 10 * * *")
    public void sendDigestNotifications() {
        log.info("Отправка дайджеста уведомлений...");
        batchUpdates.forEach((chatId, updates) -> {
            StringBuilder digest = new StringBuilder("Дайджест обновлений:\n");
            updates.forEach(update -> {
                digest.append(update.getUrl())
                        .append(" - ")
                        .append(update.getDescription())
                        .append("\n");
            });
            String validUrl =
                    updates.stream().findFirst().map(LinkUpdate::getUrl).orElse("http://localhost");
            try {
                botClient.notifyUpdate(chatId, validUrl, digest.toString(), Set.of(chatId));
            } catch (Exception e) {
                log.error("Ошибка отправки уведомления для chatId: {}", chatId, e);
            }
        });
        batchUpdates.clear();
    }
}
