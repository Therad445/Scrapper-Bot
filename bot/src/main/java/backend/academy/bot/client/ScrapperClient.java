package backend.academy.bot.client;

import backend.academy.bot.config.BotProperties;
import backend.academy.bot.dto.AddLinkRequest;
import backend.academy.bot.dto.LinkResponse;
import backend.academy.bot.dto.ListLinksResponse;
import backend.academy.bot.dto.RemoveLinkRequest;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
public class ScrapperClient {

    private final RestTemplate restTemplate;
    private final URI scrapperBaseUrl;

    public ScrapperClient(RestTemplate restTemplate, BotProperties config) {
        this.restTemplate = restTemplate;
        this.scrapperBaseUrl = URI.create(config.getScrapperUrl());
    }

    public void registerUser(Long chatId) {
        URI uri = buildPath("/tg-chat/" + chatId);
        try {
            restTemplate.postForEntity(uri, null, Void.class);
        } catch (RestClientException e) {
            log.error("Ошибка при регистрации пользователя {}", chatId, e);
            throw e;
        }
    }

    public ListLinksResponse getLinks(Long chatId) {
        URI uri = buildPath("/links");
        try {
            HttpEntity<?> entity = new HttpEntity<>(headers(chatId));
            ResponseEntity<ListLinksResponse> response =
                    restTemplate.exchange(uri, HttpMethod.GET, entity, ListLinksResponse.class);
            ListLinksResponse body = response.getBody();
            if (body == null) {
                throw new IllegalStateException("Scrapper вернул пустой ответ при получении ссылок");
            }
            return body;
        } catch (RestClientException e) {
            log.error("Ошибка при получении ссылок пользователя {}", chatId, e);
            throw e;
        }
    }

    public LinkResponse trackLink(Long chatId, URI url, List<String> tags, List<String> filters) {
        URI uri = buildPath("/links");
        AddLinkRequest requestBody = new AddLinkRequest();
        requestBody.setLink(url);
        requestBody.setTags(tags);
        requestBody.setFilters(filters);

        try {
            HttpEntity<AddLinkRequest> entity = new HttpEntity<>(requestBody, headers(chatId));
            ResponseEntity<LinkResponse> response = restTemplate.postForEntity(uri, entity, LinkResponse.class);
            LinkResponse body = response.getBody();
            if (body == null) {
                throw new IllegalStateException("Scrapper вернул пустой ответ при добавлении ссылки");
            }
            log.info(
                    "ScrapperClient: отправили запрос на Scrapper с URL {}, тэгами {} и фильтрами {}. Ответ: {}",
                    url,
                    tags,
                    filters,
                    body.getLink());
            return body;
        } catch (RestClientException e) {
            log.error("Ошибка при добавлении ссылки {}", url, e);
            throw e;
        }
    }

    public LinkResponse trackLink(Long chatId, URI url) {
        return trackLink(chatId, url, Collections.emptyList(), Collections.emptyList());
    }

    public LinkResponse untrackLink(Long chatId, URI url) {
        URI uri = buildPath("/links");
        RemoveLinkRequest body = new RemoveLinkRequest();
        body.setLink(url);

        try {
            HttpEntity<RemoveLinkRequest> entity = new HttpEntity<>(body, headers(chatId));
            ResponseEntity<LinkResponse> response =
                    restTemplate.exchange(uri, HttpMethod.DELETE, entity, LinkResponse.class);
            LinkResponse resBody = response.getBody();
            if (resBody == null) {
                throw new IllegalStateException("Scrapper вернул пустой ответ при удалении ссылки");
            }
            return resBody;
        } catch (RestClientException e) {
            log.error("Ошибка при удалении ссылки {}", url, e);
            throw e;
        }
    }

    private URI buildPath(String path) {
        return UriComponentsBuilder.fromUri(scrapperBaseUrl).path(path).build().toUri();
    }

    private HttpHeaders headers(Long chatId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Tg-chat-id", chatId.toString());
        return headers;
    }
}
