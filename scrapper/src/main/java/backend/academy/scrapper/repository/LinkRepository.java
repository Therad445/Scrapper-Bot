package backend.academy.scrapper.repository;

import backend.academy.scrapper.dto.LinkInfo;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class LinkRepository {
    private final Map<Long, Set<LinkInfo>> dataBase = new ConcurrentHashMap<>();

    public List<LinkInfo> getLinks(Long chatId) {
        return new ArrayList<>(dataBase.getOrDefault(chatId, Collections.emptySet()));
    }

    public void addLink(Long chatId, LinkInfo linkInfo) {
        dataBase.computeIfAbsent(chatId, k -> new HashSet<>()).add(linkInfo);
    }

    public Optional<LinkInfo> removeLink(Long chatId, String link) {
        Set<LinkInfo> links = dataBase.get(chatId);
        if (links == null) {
            return Optional.empty();
        }
        Optional<LinkInfo> removedLink = links.stream()
            .filter(linkInfo -> linkInfo.getLink().equals(link))
            .findFirst();
        removedLink.ifPresent(linkInfo -> {
            links.remove(linkInfo);
            if (links.isEmpty()) {
                dataBase.remove(chatId);
            }
        });
        return removedLink;
    }
}
