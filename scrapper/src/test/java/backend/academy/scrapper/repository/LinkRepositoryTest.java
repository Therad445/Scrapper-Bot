package backend.academy.scrapper.repository;

import backend.academy.scrapper.dto.LinkInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class LinkRepositoryTest {

    private LinkRepository linkRepository;

    @BeforeEach
    void setUp() {
        linkRepository = new LinkRepository();
    }

    @Test
    void shouldAddLinkInfoWhenAddLinkMethodIsCalled() {
        // Arrange
        Long chatId = 123L;
        Set<String> tags = new HashSet<>();
        tags.add("tag1");
        Set<String> filters = new HashSet<>();
        filters.add("filter1");

        LinkInfo linkInfo = new LinkInfo("http://example.com", tags, filters);

        // Act
        linkRepository.addLink(chatId, linkInfo);
        List<LinkInfo> links = linkRepository.getLinks(chatId);

        // Assert
        assertEquals(1, links.size());
        LinkInfo retrievedLink = links.get(0);
        assertEquals(linkInfo.getLink(), retrievedLink.getLink());
        assertEquals(linkInfo.getTags(), retrievedLink.getTags());
        assertEquals(linkInfo.getFilters(), retrievedLink.getFilters());
    }

    @Test
    void shouldReturnEmptyListWhenNoLinksExistForChatId() {
        // Arrange
        Long chatId = 123L;

        // Act
        List<LinkInfo> links = linkRepository.getLinks(chatId);

        // Assert
        assertTrue(links.isEmpty());
    }

    @Test
    void shouldRemoveLinkWhenRemoveLinkMethodIsCalled() {
        // Arrange
        Long chatId = 123L;
        Set<String> tags = new HashSet<>();
        tags.add("tag1");
        Set<String> filters = new HashSet<>();
        filters.add("filter1");

        LinkInfo linkInfo = new LinkInfo("http://example.com", tags, filters);
        linkRepository.addLink(chatId, linkInfo);

        // Act
        Optional<LinkInfo> removedLink = linkRepository.removeLink(chatId, "http://example.com");

        // Assert
        assertTrue(removedLink.isPresent());
        assertEquals(linkInfo.getLink(), removedLink.get().getLink());
        assertEquals(linkInfo.getTags(), removedLink.get().getTags());
        assertEquals(linkInfo.getFilters(), removedLink.get().getFilters());

        // Ensure link is removed from the repository
        List<LinkInfo> links = linkRepository.getLinks(chatId);
        assertTrue(links.isEmpty());
    }

    @Test
    void shouldReturnEmptyOptionalWhenLinkDoesNotExistForRemoval() {
        // Arrange
        Long chatId = 123L;
        Set<String> tags = new HashSet<>();
        tags.add("tag1");
        Set<String> filters = new HashSet<>();
        filters.add("filter1");

        LinkInfo linkInfo = new LinkInfo("http://example.com", tags, filters);
        linkRepository.addLink(chatId, linkInfo);

        // Act
        Optional<LinkInfo> removedLink = linkRepository.removeLink(chatId, "http://nonexistent.com");

        // Assert
        assertFalse(removedLink.isPresent());
    }

    @Test
    void shouldRemoveLinkChatEntryWhenAllLinksAreRemoved() {
        // Arrange
        Long chatId = 123L;
        Set<String> tags1 = new HashSet<>();
        tags1.add("tag1");
        Set<String> filters1 = new HashSet<>();
        filters1.add("filter1");

        Set<String> tags2 = new HashSet<>();
        tags2.add("tag2");
        Set<String> filters2 = new HashSet<>();
        filters2.add("filter2");

        LinkInfo linkInfo1 = new LinkInfo("http://example1.com", tags1, filters1);
        LinkInfo linkInfo2 = new LinkInfo("http://example2.com", tags2, filters2);

        linkRepository.addLink(chatId, linkInfo1);
        linkRepository.addLink(chatId, linkInfo2);

        // Act
        linkRepository.removeLink(chatId, "http://example1.com");
        linkRepository.removeLink(chatId, "http://example2.com");

        // Assert
        List<LinkInfo> links = linkRepository.getLinks(chatId);
        assertTrue(links.isEmpty());
    }

    @Test
    void shouldReturnAllLinksForAllChatIds() {
        // Arrange
        Long chatId1 = 123L;
        Long chatId2 = 456L;

        Set<String> tags1 = new HashSet<>();
        tags1.add("tag1");
        Set<String> filters1 = new HashSet<>();
        filters1.add("filter1");

        Set<String> tags2 = new HashSet<>();
        tags2.add("tag2");
        Set<String> filters2 = new HashSet<>();
        filters2.add("filter2");

        LinkInfo linkInfo1 = new LinkInfo("http://example1.com", tags1, filters1);
        LinkInfo linkInfo2 = new LinkInfo("http://example2.com", tags2, filters2);

        LinkRepository linkRepository = new LinkRepository();
        linkRepository.addLink(chatId1, linkInfo1);
        linkRepository.addLink(chatId2, linkInfo2);

        // Act
        Map<Long, List<LinkInfo>> allLinks = linkRepository.getAllLinks();

        // Assert
        assertEquals(2, allLinks.size());
        assertTrue(allLinks.containsKey(chatId1));
        assertTrue(allLinks.containsKey(chatId2));
        assertEquals(1, allLinks.get(chatId1).size());
        assertEquals(1, allLinks.get(chatId2).size());
        assertEquals(linkInfo1.getLink(), allLinks.get(chatId1).get(0).getLink());
        assertEquals(linkInfo2.getLink(), allLinks.get(chatId2).get(0).getLink());
    }

    @Test
    void shouldAddLinkForNewChatId() {
        // Arrange
        Long chatId = 789L;
        Set<String> tags = new HashSet<>();
        tags.add("tag1");
        Set<String> filters = new HashSet<>();
        filters.add("filter1");

        LinkInfo linkInfo = new LinkInfo("http://example.com", tags, filters);

        LinkRepository linkRepository = new LinkRepository();

        // Act
        linkRepository.addLink(chatId, linkInfo);
        List<LinkInfo> links = linkRepository.getLinks(chatId);

        // Assert
        assertEquals(1, links.size());
        LinkInfo retrievedLink = links.get(0);
        assertEquals(linkInfo.getLink(), retrievedLink.getLink());
        assertEquals(linkInfo.getTags(), retrievedLink.getTags());
        assertEquals(linkInfo.getFilters(), retrievedLink.getFilters());
    }

    @Test
    void shouldReturnEmptyListWhenChatIdHasNoLinks() {
        // Arrange
        Long chatId = 999L;
        LinkRepository linkRepository = new LinkRepository();

        // Act
        List<LinkInfo> links = linkRepository.getLinks(chatId);

        // Assert
        assertTrue(links.isEmpty());
    }

    @Test
    void shouldNotRemoveLinkFromNonExistentChatId() {
        // Arrange
        Long chatId = 999L;
        LinkRepository linkRepository = new LinkRepository();
        Set<String> tags = new HashSet<>();
        tags.add("tag1");
        Set<String> filters = new HashSet<>();
        filters.add("filter1");

        LinkInfo linkInfo = new LinkInfo("http://example.com", tags, filters);
        linkRepository.addLink(123L, linkInfo);

        // Act
        Optional<LinkInfo> removedLink = linkRepository.removeLink(chatId, "http://example.com");

        // Assert
        assertFalse(removedLink.isPresent());
    }
}
