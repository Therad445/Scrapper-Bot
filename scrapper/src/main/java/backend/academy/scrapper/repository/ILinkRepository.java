package backend.academy.scrapper.repository;

import backend.academy.scrapper.dto.LinkInfo;
import java.util.List;
import java.util.Optional;

public interface ILinkRepository {
    List<LinkInfo> getLinks(Long chatId);
    void addLink(Long chatId, LinkInfo linkInfo);
    Optional<LinkInfo> removeLink(Long chatId, String link);
}
