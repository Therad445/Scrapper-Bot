package backend.academy.scrapper.service.impl;

import backend.academy.scrapper.model.LinkInfo;
import backend.academy.scrapper.service.LinkChecker;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
class StackOverflowChecker implements LinkChecker {

    private static final Pattern ANSWER = Pattern.compile("https://stackoverflow\\.com/a/(?<id>\\d+)(/.*)?");
    private static final Pattern COMMENT =
            Pattern.compile("https://stackoverflow\\.com/questions/\\d+/.+?#comment(?<id>\\d+)_\\d+");

    private final RestTemplate restTemplate;

    @Value("${app.stackoverflow.key:}")
    private String apiKey;

    private String preview;
    private Instant remoteUpdated;

    @Override
    public boolean supports(LinkInfo link) {
        return ANSWER.matcher(link.url()).matches()
                || COMMENT.matcher(link.url()).matches();
    }

    @Override
    public boolean hasUpdates(LinkInfo li) {
        Matcher mAns = ANSWER.matcher(li.url());
        Matcher mCom = COMMENT.matcher(li.url());
        if (!mAns.matches() && !mCom.matches()) return false;
        String id = mAns.matches() ? mAns.group("id") : mCom.group("id");
        boolean isComment = mCom.matches();

        Map<String, Object> post =
                fetch("https://api.stackexchange.com/2.3/" + (isComment ? "comments/" : "answers/") + id);
        if (post == null) return false;

        long creation = ((Number) post.get("creation_date")).longValue();
        remoteUpdated = Instant.ofEpochSecond(creation);
        if (li.lastUpdatedAt() != null && !remoteUpdated.isAfter(li.lastUpdatedAt())) return false;

        String body = (String) post.getOrDefault("body_markdown", "");
        body = body.replaceAll("<[^>]*>", "");
        if (body.length() > 200) body = body.substring(0, 200) + "…";

        long questionId = ((Number) post.get("question_id")).longValue();
        Map<String, Object> q = fetch("https://api.stackexchange.com/2.3/questions/" + questionId);
        String title = q == null ? "(unknown)" : (String) q.get("title");
        Object ownerObj = post.get("owner");
        String author = "anonymous";
        if (ownerObj instanceof Map<?, ?> ownerMap) {
            Object name = ownerMap.get("display_name");
            if (name instanceof String s) {
                author = s;
            }
        }

        preview = String.format(
                        "Stack Overflow: %s%nАвтор: %s%nСоздано: %s%n%s",
                        title, author, remoteUpdated.toString().substring(0, 10), body)
                .strip();

        return true;
    }

    private Map<String, Object> fetch(String url) {
        String finalUrl = url + "?site=stackoverflow&filter=withbody";
        if (StringUtils.hasText(apiKey)) finalUrl += "&key=" + apiKey;

        Map<?, ?> response = restTemplate.getForObject(finalUrl, Map.class);
        if (response == null || !(response.get("items") instanceof List<?> items) || items.isEmpty()) return null;

        return (Map<String, Object>) items.get(0);
    }

    @Override
    public String preview() {
        return preview == null ? "" : preview;
    }

    @Override
    public Instant remoteUpdatedAt() {
        return remoteUpdated;
    }
}
