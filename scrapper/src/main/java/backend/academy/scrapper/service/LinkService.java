package backend.academy.scrapper.service;

import backend.academy.scrapper.model.AddLinkRequest;
import backend.academy.scrapper.model.LinkResponse;
import backend.academy.scrapper.model.ListLinksResponse;
import backend.academy.scrapper.model.RemoveLinkRequest;
import backend.academy.scrapper.repository.ChatRepository;
import backend.academy.scrapper.repository.LinkRepository;
import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkService {
    private final ChatRepository chatRepository;

    private final LinkRepository linkRepository;

    @Transactional
    public ListLinksResponse getLinks(long chatId) {
        var list = linkRepository.findAllByChat(chatId);
        var resp = list.stream()
                .map(l -> new LinkResponse(
                        l.id(), l.url(),
                        l.tags(), l.filters()))
                .toList();
        return new ListLinksResponse(resp, resp.size());
    }

    @Transactional
    public LinkResponse addLink(long chatId, AddLinkRequest req) {
        chatRepository.register(chatId);
        linkRepository.add(chatId, req.link(), req.tags(), req.filters());
        return new LinkResponse(null, req.link(), req.tags(), req.filters());
    }

    @Transactional
    public LinkResponse removeLinks(long tgChatId, RemoveLinkRequest request) {
        return linkRepository
                .remove(tgChatId, request.link())
                .map(link -> new LinkResponse(null, request.link(), Set.of(), Set.of()))
                .orElseThrow(() -> new IllegalArgumentException("Ссылка не найдена!"));
    }

    @Transactional
    public void addTag(long chatId, long linkId, String tag) {
        linkRepository.addTag(chatId, linkId, tag);
    }

    @Transactional
    public void deleteTag(long chatId, long linkId, String tag) {
        linkRepository.removeTag(chatId, linkId, tag);
    }

    @Transactional
    public void addFilter(long chatId, long linkId, String filter) {
        linkRepository.addFilter(chatId, linkId, filter);
    }

    @Transactional
    public void removeFilter(long chatId, long linkId, String filter) {
        linkRepository.removeFilter(chatId, linkId, filter);
    }
}
