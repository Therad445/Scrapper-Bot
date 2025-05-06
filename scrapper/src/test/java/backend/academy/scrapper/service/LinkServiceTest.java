package backend.academy.scrapper.service;

import backend.academy.scrapper.model.AddLinkRequest;
import backend.academy.scrapper.model.LinkInfo;
import backend.academy.scrapper.model.LinkResponse;
import backend.academy.scrapper.model.RemoveLinkRequest;
import backend.academy.scrapper.repository.ChatRepository;
import backend.academy.scrapper.repository.LinkRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LinkServiceTest {

    ChatRepository chatRepository;
    LinkRepository linkRepository;
    LinkService service;

    @BeforeEach
    void setUp() {
        chatRepository = mock(ChatRepository.class);
        linkRepository = mock(LinkRepository.class);
        service = new LinkService(chatRepository, linkRepository);
    }

    @Test
    void testAddLink_shouldRegisterChatAndSave() {
        long chatId = 1L;
        String url = "https://example.com";
        Set<String> tags = Set.of("tag1");
        Set<String> filters = Set.of("user:bob");

        AddLinkRequest request = new AddLinkRequest(url, tags, filters);

        LinkResponse result = service.addLink(chatId, request);

        verify(chatRepository).register(chatId);
        verify(linkRepository).add(chatId, url, tags, filters);
        assertEquals(url, result.link());
        assertEquals(tags, result.tags());
        assertEquals(filters, result.filters());
    }

    @Test
    void testGetLinks_shouldReturnMappedResponse() {
        long chatId = 42L;
        String url = "https://site.com";
        var linkEntity = new LinkInfo(99L, url, null, null, Set.of("a"), Set.of("filter"));

        when(linkRepository.findAllByChat(chatId)).thenReturn(List.of(linkEntity));

        var result = service.getLinks(chatId);

        assertEquals(1, result.size());
        assertEquals(url, result.links().getFirst().link());
        assertEquals(Set.of("a"), result.links().getFirst().tags());
        assertEquals(Set.of("filter"), result.links().getFirst().filters());
    }

    @Test
    void testRemoveLinks_shouldRemoveIfExists() {
        long chatId = 1L;
        String url = "https://remove.com";
        RemoveLinkRequest request = new RemoveLinkRequest(url);

        var linkInfo = new LinkInfo(1L, url, null, null, Set.of(), Set.of());

        when(linkRepository.remove(chatId, url)).thenReturn(Optional.of(linkInfo));

        LinkResponse result = service.removeLinks(chatId, request);

        assertEquals(url, result.link());
        assertEquals(Set.of(), result.tags());
        assertEquals(Set.of(), result.filters());
    }

    @Test
    void testRemoveLinks_shouldThrowIfNotExists() {
        long chatId = 1L;
        String url = "https://notfound.com";
        RemoveLinkRequest request = new RemoveLinkRequest(url);

        when(linkRepository.remove(chatId, url)).thenReturn(Optional.empty());

        var ex = assertThrows(IllegalArgumentException.class, () -> service.removeLinks(chatId, request));
        assertEquals("Ссылка не найдена!", ex.getMessage());
    }

    @Test
    void testAddTag_shouldCallRepo() {
        service.addTag(1L, 2L, "tag");
        verify(linkRepository).addTag(1L, 2L, "tag");
    }

    @Test
    void testDeleteTag_shouldCallRepo() {
        service.deleteTag(1L, 2L, "tag");
        verify(linkRepository).removeTag(1L, 2L, "tag");
    }

    @Test
    void testAddFilter_shouldCallRepo() {
        service.addFilter(1L, 2L, "f");
        verify(linkRepository).addFilter(1L, 2L, "f");
    }

    @Test
    void testRemoveFilter_shouldCallRepo() {
        service.removeFilter(1L, 2L, "f");
        verify(linkRepository).removeFilter(1L, 2L, "f");
    }
}
