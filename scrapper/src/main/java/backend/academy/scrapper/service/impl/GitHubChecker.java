package backend.academy.scrapper.service.impl;

import backend.academy.scrapper.model.LinkInfo;
import backend.academy.scrapper.service.LinkChecker;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class GitHubChecker implements LinkChecker {
    private static final Pattern PATTERN =
            Pattern.compile("https://github\\.com/(?<owner>[^/]+)/(?<repo>[^/]+)/(issues|pull)/(?<num>\\d+)");

    private final RestTemplate restTemplate;

    @Value("${app.github-token:}")
    private String token;

    private String preview;
    private Instant remoteUpdated;

    @Override
    public boolean supports(LinkInfo link) {
        return PATTERN.matcher(link.url()).matches();
    }

    @Override
    public boolean hasUpdates(LinkInfo li) {
        Matcher m = PATTERN.matcher(li.url());
        if (!m.matches()) return false;

        String owner = m.group("owner");
        String repo = m.group("repo");
        String num = m.group("num");
        String url = "https://api.github.com/repos/%s/%s/issues/%s".formatted(owner, repo, num);

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "scrapper");
        if (!token.isBlank()) headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<GitHubIssue> response = restTemplate.exchange(url, HttpMethod.GET, entity, GitHubIssue.class);
        GitHubIssue resp = response.getBody();
        if (resp == null) return false;

        remoteUpdated = Instant.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(resp.updated_at()));
        if (li.lastUpdatedAt() == null || remoteUpdated.isAfter(li.lastUpdatedAt())) {
            preview = buildPreview(resp);
            return true;
        }
        return false;
    }

    private String buildPreview(GitHubIssue resp) {
        String body = resp.body() == null ? "" : resp.body();
        if (body.length() > 200) body = body.substring(0, 200) + "…";
        return String.format(
                        "GitHub: %s%nАвтор: %s%nСоздано: %s%n%s",
                        resp.title(), resp.user().login(), resp.created_at().substring(0, 10), body)
                .strip();
    }

    @Override
    public String preview() {
        return preview == null ? "" : preview;
    }

    @Override
    public Instant remoteUpdatedAt() {
        return remoteUpdated;
    }

    private record GitHubIssue(String title, User user, String created_at, String updated_at, String body) {}

    private record User(String login) {}
}
