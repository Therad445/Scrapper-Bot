package backend.academy.scrapper.repository.orm;

import backend.academy.scrapper.dto.LinkInfo;
import backend.academy.scrapper.repository.ILinkRepository;
import java.util.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "access-type", havingValue = "ORM")
public class OrmLinkRepository implements ILinkRepository {
    private final LinkJpaRepository repo;

    public OrmLinkRepository(LinkJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<LinkInfo> getLinks(Long chatId) {
        return repo.findByChatId(chatId).stream().map(e -> {
            var tags = parseSet(e.getTags());
            var filters = parseSet(e.getFilters());
            LinkInfo info = new LinkInfo(e.getLink(), tags, filters);
            info.getUpdateInfo().setLastUpdated(e.getLastUpdated());
            return info;
        }).toList();
    }

    @Override
    public void addLink(Long chatId, LinkInfo li) {
        if (repo.findByChatIdAndLink(chatId, li.getLink()).isPresent()) return;
        var e = new LinkEntity();
        e.setChatId(chatId);
        e.setLink(li.getLink());
        e.setTags(stringify(li.getTags()));
        e.setFilters(stringify(li.getFilters()));
        e.setLastUpdated("");
        repo.save(e);
    }

    @Override
    public Optional<LinkInfo> removeLink(Long chatId, String link) {
        return repo.findByChatIdAndLink(chatId, link)
            .map(e -> {
                repo.delete(e);
                var li = new LinkInfo(e.getLink(), parseSet(e.getTags()), parseSet(e.getFilters()));
                li.getUpdateInfo().setLastUpdated(e.getLastUpdated());
                return li;
            });
    }

    private static Set<String> parseSet(String s) {
        if (s == null || s.isBlank()) return Collections.emptySet();
        return new HashSet<>(Arrays.asList(s.split(",")));
    }
    private static String stringify(Set<String> set) {
        return set == null || set.isEmpty() ? "" : String.join(",", set);
    }
}
