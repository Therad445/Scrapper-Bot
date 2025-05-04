package backend.academy.scrapper.repository.orm;

import backend.academy.scrapper.entity.ChatEntity;
import backend.academy.scrapper.entity.LinkEntity;
import backend.academy.scrapper.model.LinkInfo;
import backend.academy.scrapper.repository.LinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "access-type", havingValue = "ORM")
public class OrmLinkRepository implements LinkRepository {

    private final LinkJpaRepository linkJpa;
    private final ChatJpaRepository chatJpa;

    @Override
    @Transactional
    public void add(long chatId, String url) {
        ChatEntity chat = chatJpa.findById(chatId)
            .orElseThrow(() -> new IllegalArgumentException("Chat not found: " + chatId));

        LinkEntity link = linkJpa.findByUrl(url)
            .orElseGet(() -> {
                LinkEntity newLink = new LinkEntity();
                newLink.url(url);
                newLink.lastCheckedAt(Instant.EPOCH);
                return linkJpa.save(newLink);
            });

        chat.links().add(link);
        chatJpa.save(chat);
    }

    @Override
    @Transactional
    public Optional<LinkInfo> remove(long chatId, String url) {
        Optional<ChatEntity> chatOpt = chatJpa.findById(chatId);
        Optional<LinkEntity> linkOpt = linkJpa.findByUrl(url);

        if (chatOpt.isPresent() && linkOpt.isPresent()) {
            ChatEntity chat = chatOpt.get();
            LinkEntity link = linkOpt.get();

            boolean removed = chat.links().remove(link);
            if (removed) {
                chatJpa.save(chat);
                return Optional.of(new LinkInfo(link.id(), link.url()));
            }
        }
        return Optional.empty();
    }

    @Override
    public Page<LinkInfo> findLinksForCheck(Instant threshold, Pageable pageable) {
        return linkJpa.findOldLinks(threshold, pageable)
            .map(link -> new LinkInfo(link.id(), link.url()));
    }

    @Override
    @Transactional
    public void updateCheckTime(long linkId, Instant checkedAt, Instant updatedAt) {
        LinkEntity link = linkJpa.findById(linkId)
            .orElseThrow(() -> new IllegalArgumentException("Link not found: " + linkId));

        link.lastCheckedAt(checkedAt);
        if (updatedAt != null) {
            link.lastUpdatedAt(updatedAt);
        }
        linkJpa.save(link);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LinkInfo> findAllByChat(long chatId) {
        ChatEntity chat = chatJpa.findById(chatId)
            .orElseThrow(() -> new IllegalArgumentException("Chat not found: " + chatId));

        return chat.links().stream()
            .map(link -> new LinkInfo(link.id(), link.url()))
            .toList();
    }
}
