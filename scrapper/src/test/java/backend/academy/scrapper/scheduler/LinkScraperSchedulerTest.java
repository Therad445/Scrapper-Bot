package backend.academy.scrapper.scheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.scrapper.config.ScrapperConfig;
import backend.academy.scrapper.config.ScrapperConfig.Scheduler;
import backend.academy.scrapper.model.LinkInfo;
import backend.academy.scrapper.model.LinkUpdate;
import backend.academy.scrapper.notification.NotificationService;
import backend.academy.scrapper.repository.ChatRepository;
import backend.academy.scrapper.repository.LinkRepository;
import backend.academy.scrapper.service.LinkChecker;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;

public class LinkScraperSchedulerTest {

    private LinkRepository linkRepository;
    private ChatRepository chatRepository;
    private NotificationService notifier;
    private LinkChecker checker;
    private LinkScraperScheduler scheduler;

    @BeforeEach
    public void setup() {
        linkRepository = mock(LinkRepository.class);
        chatRepository = mock(ChatRepository.class);
        notifier = mock(NotificationService.class);
        checker = mock(LinkChecker.class);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        List<LinkChecker> checkers = List.of(checker);

        Scheduler schedulerProps = mock(Scheduler.class);
        when(schedulerProps.forceCheckDelay()).thenReturn(Duration.ofMinutes(10));
        when(schedulerProps.batchSize()).thenReturn(10);

        ScrapperConfig scrapperConfig = mock(ScrapperConfig.class);
        when(scrapperConfig.scheduler()).thenReturn(schedulerProps);

        scheduler =
                new LinkScraperScheduler(scrapperConfig, linkRepository, chatRepository, checkers, notifier, executor);
    }

    @Test
    public void shouldNotifyBotWhenUpdatesExist() {
        LinkInfo link =
                new LinkInfo(1L, "http://example.com/repo", Instant.now().minusSeconds(1000), null, Set.of(), Set.of());
        when(linkRepository.findLinksForCheck(any(), any())).thenReturn(new PageImpl<>(List.of(link)));
        when(chatRepository.findChatIdsByLinkId(eq(link.id()))).thenReturn(List.of(100L, 200L));
        when(checker.supports(link)).thenReturn(true);
        when(checker.hasUpdates(link)).thenReturn(true);
        when(checker.preview()).thenReturn("Changes detected");
        when(checker.remoteUpdatedAt()).thenReturn(Instant.now());

        scheduler.run();

        ArgumentCaptor<LinkUpdate> updateCaptor = ArgumentCaptor.forClass(LinkUpdate.class);
        verify(notifier).notify(updateCaptor.capture());
        LinkUpdate update = updateCaptor.getValue();

        assertEquals(link.id(), update.id());
        assertEquals(link.url(), update.url());
        assertEquals("Changes detected", update.description());
        assertTrue(update.tgChatIds().containsAll(List.of(100L, 200L)));

        verify(linkRepository).updateCheckTime(eq(link.id()), any(), any());
    }

    @Test
    public void shouldSkipNotificationIfNoChats() {
        LinkInfo link = new LinkInfo(
                2L, "http://example.com/repo2", Instant.now().minusSeconds(1000), null, Set.of(), Set.of());

        when(linkRepository.findLinksForCheck(any(), any())).thenReturn(new PageImpl<>(List.of(link)));
        when(chatRepository.findChatIdsByLinkId(link.id())).thenReturn(List.of()); // нет чатов
        when(checker.supports(link)).thenReturn(true);
        when(checker.hasUpdates(link)).thenReturn(true);
        when(checker.preview()).thenReturn("Test update");
        when(checker.remoteUpdatedAt()).thenReturn(Instant.now());

        scheduler.run();

        verify(notifier, never()).notify(any());
        verify(linkRepository).updateCheckTime(eq(link.id()), any(), any());
    }

    @Test
    public void shouldSkipNotificationIfNoChanges() {
        LinkInfo link = new LinkInfo(
                3L, "http://example.com/repo3", Instant.now().minusSeconds(1000), null, Set.of(), Set.of());
        when(linkRepository.findLinksForCheck(any(), any())).thenReturn(new PageImpl<>(List.of(link)));
        when(chatRepository.findChatIdsByLinkId(eq(link.id()))).thenReturn(List.of(300L));
        when(checker.supports(link)).thenReturn(true);
        when(checker.hasUpdates(link)).thenReturn(false);
        when(checker.remoteUpdatedAt()).thenReturn(null);

        scheduler.run();

        verify(notifier, never()).notify(any());
        verify(linkRepository).updateCheckTime(eq(link.id()), any(), isNull());
    }

    @Test
    public void shouldSkipIfNoLinksToCheck() {
        when(linkRepository.findLinksForCheck(any(), any())).thenReturn(new PageImpl<>(List.of()));

        scheduler.run();

        verify(notifier, never()).notify(any());
        verify(linkRepository, never()).updateCheckTime(anyLong(), any(), any());
    }

    @Test
    public void shouldSkipIfNoCheckerSupportsLink() {
        LinkInfo link = new LinkInfo(4L, "http://unsupported.com", Instant.now(), null, Set.of(), Set.of());
        when(linkRepository.findLinksForCheck(any(), any())).thenReturn(new PageImpl<>(List.of(link)));
        when(chatRepository.findChatIdsByLinkId(eq(link.id()))).thenReturn(List.of(123L));
        when(checker.supports(link)).thenReturn(false);

        scheduler.run();

        verify(notifier, never()).notify(any());
        verify(linkRepository, never()).updateCheckTime(anyLong(), any(), any());
    }
}
