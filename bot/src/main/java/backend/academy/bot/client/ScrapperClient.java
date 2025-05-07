package backend.academy.bot.client;

import backend.academy.bot.dto.AddLinkRequest;
import backend.academy.bot.dto.LinkResponse;
import backend.academy.bot.dto.ListLinksResponse;
import backend.academy.bot.dto.RemoveLinkRequest;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
public class ScrapperClient {

    private final RestTemplate restTemplate;
    private final URI scrapperBaseUrl;

    public ScrapperClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.scrapperBaseUrl = URI.create("http://scrapper:8081/");
    }

    public void registerUser(Long chatId) {
        URI uri = UriComponentsBuilder.fromUri(scrapperBaseUrl)
            .path("/tg-chat/" + chatId)
            .build()
            .toUri();
        restTemplate.postForEntity(uri, null, Void.class);
    }

    public ListLinksResponse getLinks(Long chatId) {
        URI uri = UriComponentsBuilder.fromUri(scrapperBaseUrl)
            .path("/links")
            .build()
            .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Tg-chat-id", chatId.toString());
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<ListLinksResponse> response =
            restTemplate.exchange(uri, HttpMethod.GET, entity, ListLinksResponse.class);
        return response.getBody();
    }

    public LinkResponse trackLink(Long chatId, URI url, List<String> tags, List<String> filters) {
        AddLinkRequest requestBody = new AddLinkRequest();
        requestBody.setLink(url);
        requestBody.setTags(tags);
        requestBody.setFilters(filters);

        URI uri = UriComponentsBuilder.fromUri(scrapperBaseUrl)
            .path("/links")
            .build()
            .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Tg-chat-id", chatId.toString());

        HttpEntity<AddLinkRequest> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<LinkResponse> response = restTemplate.postForEntity(uri, entity, LinkResponse.class);
        log.info(
            "ScrapperClient: отправили запрос на Scrapper с URL {}, тэгами {} и фильтрами {}. Scrapper ответил: {}",
            url,
            tags,
            filters,
            Objects.requireNonNull(response.getBody()).getLink());
        return response.getBody();
    }

    public LinkResponse trackLink(Long chatId, URI url) {
        return trackLink(chatId, url, Collections.emptyList(), Collections.emptyList());
    }


    public LinkResponse untrackLink(Long chatId, URI url) {
        RemoveLinkRequest body = new RemoveLinkRequest();
        body.setLink(url);

        URI uri = UriComponentsBuilder.fromUri(scrapperBaseUrl)
            .path("/links")
            .build()
            .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Tg-chat-id", chatId.toString());

        HttpEntity<RemoveLinkRequest> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(uri, HttpMethod.DELETE, entity, LinkResponse.class)
            .getBody();
    }


}
