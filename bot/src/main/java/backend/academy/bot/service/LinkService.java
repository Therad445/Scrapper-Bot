package backend.academy.bot.service;

import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.dto.LinkResponse;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkService {

    private final ScrapperClient client;

    public LinkResponse track(Long chatId, URI url, List<String> tags, List<String> filters) {
        return client.trackLink(chatId, url, tags, filters);
    }

    public LinkResponse untrack(Long chatId, URI url) {
        return client.untrackLink(chatId, url);
    }

    public List<LinkResponse> list(Long chatId) {
        return client.getLinks(chatId).getLinks();
    }
}
