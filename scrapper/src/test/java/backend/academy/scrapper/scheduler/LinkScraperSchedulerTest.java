package backend.academy.scrapper.scheduler;

import static org.mockito.Mockito.*;

import backend.academy.scrapper.client.BotClient;
import backend.academy.scrapper.dto.LinkInfo;
import backend.academy.scrapper.model.GithubResponse;
import backend.academy.scrapper.repository.ChatRepository;
import backend.academy.scrapper.repository.InMemoryLinkRepository;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

public class LinkScraperSchedulerTest {

    private InMemoryLinkRepository inMemoryLinkRepository;
    private BotClient botClient;
    private RestTemplate restTemplate;
    private LinkScraperScheduler scheduler;
    private ChatRepository chatRepository;
    private final Long chatId = 200L;

    @BeforeEach
    public void setup() {
        // Arrange
        inMemoryLinkRepository = new InMemoryLinkRepository();
        chatRepository = mock(ChatRepository.class);
        botClient = mock(BotClient.class);
        restTemplate = mock(RestTemplate.class);
        scheduler = new LinkScraperScheduler(chatRepository, inMemoryLinkRepository, botClient, restTemplate);
    }

    @Test
    public void testSchedulerSendsUpdateOnGitHubLinkChange() throws Exception {
        // Arrange
        LinkInfo linkInfo = new LinkInfo("https://github.com/owner/repo", Set.of("tag"), Set.of("filter"));
        linkInfo.getUpdateInfo().setLastUpdated("oldDate");

        inMemoryLinkRepository.addLink(200L, linkInfo);

        GithubResponse githubResponse = new GithubResponse();
        githubResponse.setUpdated_at("newDate");
        when(restTemplate.getForObject(anyString(), eq(GithubResponse.class))).thenReturn(githubResponse);

        // Act
        scheduler.checkForUpdates();
        scheduler.sendDigestNotifications();

        // Assert
        verify(botClient, times(1)).notifyUpdate(eq(200L), anyString(), anyString(), eq(Set.of(200L)));
    }

    @Test
    public void testSchedulerDoesNotSendUpdateIfNoChange() throws Exception {
        // Arrange
        LinkInfo linkInfo = new LinkInfo("https://github.com/owner/repo", Set.of("tag"), Set.of("filter"));
        linkInfo.getUpdateInfo().setLastUpdated("sameDate");
        inMemoryLinkRepository.addLink(chatId, linkInfo);

        // Arrange
        GithubResponse githubResponse = new GithubResponse();
        githubResponse.setUpdated_at("sameDate");
        when(restTemplate.getForObject(any(), eq(GithubResponse.class))).thenReturn(githubResponse);

        // Act
        scheduler.checkForUpdates();
        scheduler.sendDigestNotifications();

        Thread.sleep(1000);

        // Assert
        verify(botClient, never()).notifyUpdate(anyLong(), anyString(), anyString(), any());
        verifyNoMoreInteractions(botClient);
    }
}
