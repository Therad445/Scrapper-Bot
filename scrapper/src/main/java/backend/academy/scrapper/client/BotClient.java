package backend.academy.scrapper.client;

import backend.academy.scrapper.model.LinkUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.util.Set;

@Component
@Slf4j
public class BotClient {

    private final RestTemplate restTemplate;
    private final String botBaseUrl;

    @Autowired
    public BotClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.botBaseUrl = "http://localhost:8080";
    }

    public Object notifyUpdate(Long id, String url, String description, Set<Long> tgChatIds) {
        LinkUpdate linkUpdate = new LinkUpdate(id, url, description, tgChatIds);
        URI uri = URI.create(botBaseUrl + "/updates");
        HttpEntity<LinkUpdate> entity = new HttpEntity<>(linkUpdate);

        try {
            ResponseEntity<?> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
            if (response == null || response.getBody() == null) {
                log.error("Error: Response is null");
                throw new IllegalArgumentException("Received empty response");
            }
            return response.getBody();
        } catch (Exception e) {
            log.error("Error occurred while notifying update", e);
            throw new IllegalArgumentException("Failed to notify update", e);
        }
    }


}
