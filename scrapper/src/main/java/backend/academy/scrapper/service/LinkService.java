package backend.academy.scrapper.service;

import backend.academy.scrapper.model.AddLinkRequest;
import backend.academy.scrapper.model.LinkInfo;
import backend.academy.scrapper.model.LinkResponse;
import backend.academy.scrapper.model.ListLinksResponse;
import backend.academy.scrapper.model.RemoveLinkRequest;
import backend.academy.scrapper.repository.ChatRepository;
import backend.academy.scrapper.repository.LinkRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;



@Service
@RequiredArgsConstructor
public class LinkService {

    private final ChatRepository chatRepository;
    private final LinkRepository linkRepository;

    public ListLinksResponse getLinks(long tgChatId) {
        List<LinkInfo> links = linkRepository.findAllByChat(tgChatId);
        List<LinkResponse> responses = links.stream()
            .map(link -> new LinkResponse(
                link.id(),
                link.url(),
                Set.of(),
                Set.of()
            ))
            .toList();
        return new ListLinksResponse(responses, responses.size());
    }

    @Transactional
    public LinkResponse addLink(long tgChatId, AddLinkRequest request) {
        chatRepository.register(tgChatId);
        linkRepository.add(tgChatId, request.link());
        return new LinkResponse(null, request.link(), Set.of(), Set.of());
    }

    @Transactional
    public LinkResponse removeLinks(long tgChatId, RemoveLinkRequest request) {
        return linkRepository
            .remove(tgChatId, request.link())
            .map(link -> new LinkResponse(null, request.link(), Set.of(), Set.of()))
            .orElseThrow(() -> new IllegalArgumentException("Ссылка не найдена!"));
    }

}
