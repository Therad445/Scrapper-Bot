package backend.academy.scrapper.repository.orm;

import backend.academy.scrapper.entity.ChatEntity;
import backend.academy.scrapper.entity.FilterEntity;
import backend.academy.scrapper.entity.LinkEntity;
import backend.academy.scrapper.entity.SubscriptionEntity;
import backend.academy.scrapper.entity.SubscriptionFilterEntity;
import backend.academy.scrapper.entity.SubscriptionTagEntity;
import backend.academy.scrapper.entity.TagEntity;
import backend.academy.scrapper.model.LinkInfo;
import backend.academy.scrapper.repository.LinkRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import static java.util.stream.Collectors.toSet;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "ORM")
public class OrmLinkRepository implements LinkRepository {

    private final LinkJpaRepository linkJpa;
    private final ChatJpaRepository chatJpa;
    private final TagJpaRepository tagJpa;
    private final FilterJpaRepository filterJpa;


    private LinkEntity createNewLink(String url) {
        LinkEntity le = new LinkEntity();
        le.url(url);
        le.lastCheckedAt(Instant.EPOCH);
        return linkJpa.save(le);
    }

    private SubscriptionEntity getOrCreateSubscription(ChatEntity chat,
                                                       LinkEntity link) {
        return chat.subscriptions().stream()
            .filter(s -> s.link().equals(link))
            .findFirst()
            .orElseGet(() -> {
                SubscriptionEntity s = new SubscriptionEntity();
                s.chat(chat);
                s.link(link);
                chat.subscriptions().add(s);
                return s;
            });
    }


    @Override
    @Transactional
    public void add(long chatId, String url,
                    Set<String> tags, Set<String> filters) {

        ChatEntity chat = chatJpa.findById(chatId)
            .orElseThrow(() ->
                new IllegalArgumentException("Chat not found: " + chatId));

        LinkEntity link = linkJpa.findByUrl(url)
            .orElseGet(() -> createNewLink(url));

        SubscriptionEntity sub = getOrCreateSubscription(chat, link);

        for (String t : tags) {
            TagEntity tag = tagJpa.findByName(t)
                .orElseGet(() -> {
                    TagEntity nt = new TagEntity();
                    nt.name(t);
                    return tagJpa.save(nt);
                });
            boolean exists = sub.tags().stream()
                .anyMatch(st -> st.tag().equals(tag));
            if (!exists) {
                SubscriptionTagEntity st = new SubscriptionTagEntity();
                st.chat(chat);
                st.link(link);
                st.tag(tag);
                sub.tags().add(st);
            }
        }

        for (String f : filters) {
            FilterEntity fe = filterJpa.findByName(f)
                .orElseGet(() -> {
                    FilterEntity nf = new FilterEntity();
                    nf.name(f);
                    return filterJpa.save(nf);
                });
            boolean exists = sub.filters().stream()
                .anyMatch(sf -> sf.filter().equals(fe));
            if (!exists) {
                SubscriptionFilterEntity sf = new SubscriptionFilterEntity();
                sf.chat(chat);
                sf.link(link);
                sf.filter(fe);
                sub.filters().add(sf);
            }
        }

        chatJpa.save(chat);
    }

    @Override
    @Transactional
    public void addTag(long chat, long link, String tag) {
        add(chat, getUrl(link), Set.of(tag), Set.of());
    }

    @Override
    @Transactional
    public void removeTag(long chat, long link, String tag) {
        chatJpa.findById(chat).ifPresent(c ->
            c.subscriptions().stream()
                .filter(s -> s.link().id() == link)
                .findFirst()
                .ifPresent(s ->
                    s.tags().removeIf(
                        st -> st.tag().name().equals(tag))));
    }

    @Override
    @Transactional
    public void addFilter(long chat, long link, String filter) {
        add(chat, getUrl(link), Set.of(), Set.of(filter));
    }

    @Override
    @Transactional
    public void removeFilter(long chat, long link, String filter) {
        chatJpa.findById(chat).ifPresent(c ->
            c.subscriptions().stream()
                .filter(s -> s.link().id() == link)
                .findFirst()
                .ifPresent(s ->
                    s.filters().removeIf(
                        sf -> sf.filter().name().equals(filter))));
    }


    @Override
    @Transactional
    public Optional<LinkInfo> remove(long chatId, String url) {
        ChatEntity chat = chatJpa.findById(chatId).orElse(null);
        LinkEntity link = linkJpa.findByUrl(url).orElse(null);
        if (chat == null || link == null) return Optional.empty();

        boolean deleted = chat.subscriptions()
            .removeIf(s -> s.link().equals(link));

        return deleted
            ? Optional.of(new LinkInfo(
            link.id(), link.url(),
            link.lastCheckedAt(), link.lastUpdatedAt(),
            Set.of(), Set.of()))
            : Optional.empty();

    }

    @Override
    public Page<LinkInfo> findLinksForCheck(Instant th, Pageable p) {
        return linkJpa.findOldLinks(th, p)
            .map(l -> new LinkInfo(
                l.id(), l.url(),
                l.lastCheckedAt(), l.lastUpdatedAt(),
                Set.of(), Set.of()));
    }

    @Override
    public void updateCheckTime(long id, Instant check, Instant upd) {
        linkJpa.findById(id).ifPresent(l -> {
            l.lastCheckedAt(check);
            if (upd != null) l.lastUpdatedAt(upd);
            linkJpa.save(l);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<LinkInfo> findAllByChat(long chatId) {
        return chatJpa.findById(chatId)
            .map(c -> c.subscriptions().stream()
                .map(this::toInfo).toList())
            .orElse(List.of());
    }

    private LinkInfo toInfo(SubscriptionEntity s) {
        return new LinkInfo(
            s.link().id(),
            s.link().url(),
            s.link().lastCheckedAt(), s.link().lastUpdatedAt(),
            s.tags().stream().map(st -> st.tag().name()).collect(toSet()),
            s.filters().stream().map(sf -> sf.filter().name()).collect(toSet()));
    }

    private String getUrl(long linkId) {
        return linkJpa.findById(linkId)
            .map(LinkEntity::url)
            .orElseThrow();
    }
}
