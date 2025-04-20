package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.LinkInfo;
import backend.academy.scrapper.model.AddLinkRequest;
import backend.academy.scrapper.model.LinkResponse;
import backend.academy.scrapper.model.ListLinksResponse;
import backend.academy.scrapper.model.RemoveLinkRequest;
import backend.academy.scrapper.repository.ILinkRepository ;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LinkService {
    private final ILinkRepository  linkRepository;

    public LinkService(ILinkRepository  linkRepository) {
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
        boolean linkExists = linkRepository.getLinks(chatId).stream()
                .anyMatch(existingLink -> existingLink.getLink().equals(linkInfo.getLink()));
        if (linkExists) {
            log.info("Ссылка уже существует: chatId={}, url={}", chatId, linkInfo.getLink());
            return null;
        }
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
