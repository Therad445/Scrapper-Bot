package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.LinkInfo;
import backend.academy.scrapper.model.AddLinkRequest;
import backend.academy.scrapper.model.LinkResponse;
import backend.academy.scrapper.model.ListLinksResponse;
import backend.academy.scrapper.model.RemoveLinkRequest;
import backend.academy.scrapper.repository.LinkRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LinkService {
    private final LinkRepository linkRepository;

    public LinkService(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    public ListLinksResponse getLinks(Long chatId) {
        List<LinkInfo> links = linkRepository.getLinks(chatId);
        List<LinkResponse> linkResponses = links.stream()
                .map(linkInfo ->
                        new LinkResponse(chatId, linkInfo.getLink(), linkInfo.getTags(), linkInfo.getFilters()))
                .toList();
        return new ListLinksResponse(linkResponses, linkResponses.size());
    }

    public LinkResponse addLink(Long chatId, AddLinkRequest addLinkRequest) {
        LinkInfo linkInfo =
                new LinkInfo(addLinkRequest.getLink(), addLinkRequest.getTags(), addLinkRequest.getFilters());
        linkRepository.addLink(chatId, linkInfo);
        log.info("Scrapper сохранил: chatId={}, url={}", chatId, linkInfo.getLink());
        return new LinkResponse(
                chatId, addLinkRequest.getLink(), addLinkRequest.getTags(), addLinkRequest.getFilters());
    }

    public LinkResponse removeLinks(Long chatId, RemoveLinkRequest removeLinkRequest) {
        LinkInfo linkInfo = linkRepository
                .removeLink(chatId, removeLinkRequest.getLink())
                .orElseThrow(() -> new IllegalArgumentException("Ссылка не найдена!"));
        return new LinkResponse(chatId, linkInfo.getLink(), linkInfo.getTags(), linkInfo.getFilters());
    }
}
