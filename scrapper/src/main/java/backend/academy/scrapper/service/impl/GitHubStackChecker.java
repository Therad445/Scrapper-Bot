package backend.academy.scrapper.service.impl;

import backend.academy.scrapper.model.LinkInfo;
import backend.academy.scrapper.service.LinkChecker;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GitHubStackChecker implements LinkChecker {
    private String preview;

    @Override
    public boolean hasUpdates(LinkInfo li) {
        try (HttpClient c = HttpClient.newHttpClient()) {
            HttpRequest req = HttpRequest.newBuilder(new URI(li.url()))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();
            var resp = c.send(req, HttpResponse.BodyHandlers.discarding());
            var etag = resp.headers().firstValue("etag").orElse("");
            if (!etag.equals(String.valueOf(li.id()))) {
                preview = "Обновлён ETag: " + etag;
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    @Override
    public String preview() {
        return preview == null ? "" : preview;
    }
}
