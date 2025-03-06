package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.LinkInfo;
import backend.academy.scrapper.model.AddLinkRequest;
import backend.academy.scrapper.model.LinkResponse;
import backend.academy.scrapper.model.ListLinksResponse;
import backend.academy.scrapper.model.RemoveLinkRequest;
import backend.academy.scrapper.repository.LinkRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LinkService {
    private final LinkRepository linkRepository;

    public LinkService(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    public ListLinksResponse get(Long chatId) {
        List<LinkInfo> links = linkRepository.get(chatId);
        List<LinkResponse> linkResponses = links.stream()
            .map(linkInfo -> new LinkResponse(chatId, linkInfo.getLink(), linkInfo.getTags(), linkInfo.getFilters()))
            .toList();
        return new ListLinksResponse(linkResponses, linkResponses.size());
    }

    public LinkResponse add(Long chatId, AddLinkRequest addLinkRequest) {
        LinkInfo linkInfo = new LinkInfo(addLinkRequest.getLink(), addLinkRequest.getTags(), addLinkRequest.getFilters());
        linkRepository.add(chatId, linkInfo);
        return new LinkResponse(chatId, addLinkRequest.getLink(), addLinkRequest.getTags(), addLinkRequest.getFilters());
    }

    public LinkResponse remove(Long chatId, RemoveLinkRequest removeLinkRequest) {
        Optional<LinkInfo> removedLinkInfo = linkRepository.remove(chatId, removeLinkRequest.getLink());
        if (removedLinkInfo.isEmpty()) {
            throw new IllegalArgumentException("Ссылка не найдена!");
        }
        LinkInfo linkInfo = removedLinkInfo.get();
        return new LinkResponse(chatId, linkInfo.getLink(), linkInfo.getTags(), linkInfo.getFilters());
    }
}
