package backend.academy.scrapper.controller;

import backend.academy.scrapper.model.AddLinkRequest;
import backend.academy.scrapper.model.LinkResponse;
import backend.academy.scrapper.model.ListLinksResponse;
import backend.academy.scrapper.model.RemoveLinkRequest;
import backend.academy.scrapper.service.LinkService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/links")
@Validated
public class LinksController {
    private final LinkService linkService;

    @Autowired
    public LinksController(LinkService linkService) {
        this.linkService = linkService;
    }

    @GetMapping
    public ResponseEntity<?> getLinks(@RequestHeader("Tg-chat-id") @NotNull @Positive Long chatId) {
        ListLinksResponse listLinksResponse = linkService.getLinks(chatId);
        log.info("Ссылки успешно получены");
        return ResponseEntity.ok().body(listLinksResponse);
    }

    @PostMapping
    public ResponseEntity<?> addLink(
        @RequestHeader("Tg-chat-id") @NotNull @Positive Long chatId,
        @RequestBody @Valid AddLinkRequest addLinkRequest) {
        LinkResponse linkResponse = linkService.addLink(chatId, addLinkRequest);
        log.info("Ссылка успешно добавлена");
        return ResponseEntity.ok().body(linkResponse);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteLink(
        @RequestHeader("Tg-chat-id") @NotNull @Positive Long chatId,
        @RequestBody @Valid RemoveLinkRequest removeLinkRequest) {
        LinkResponse linkResponse = linkService.removeLinks(chatId, removeLinkRequest);
        log.info("Ссылка успешно убрана");
        return ResponseEntity.ok().body(linkResponse);
    }

    @PostMapping("/{id}/tags")
    public void addTag(@PathVariable long id,
                       @RequestParam String tag,
                       @RequestHeader("Tg-chat-id") long chatId) {
        linkService.addTag(chatId, id, tag);
    }

    @DeleteMapping("/{id}/tags/{tag}")
    public void deleteTag(@PathVariable long id,
                          @PathVariable String tag,
                          @RequestHeader("Tg-chat-id") long chatId) {
        linkService.deleteTag(chatId, id, tag);
    }

    @PostMapping("/{id}/filters")
    public void addFilter(@PathVariable long id,
                          @RequestParam String filter,
                          @RequestHeader("Tg-chat-id") long chatId) {
        linkService.addFilter(chatId, id, filter);
    }

    @DeleteMapping("/{id}/filters/{filter}")
    public void deleteFilter(@PathVariable long id,
                             @PathVariable String filter,
                             @RequestHeader("Tg-chat-id") long chatId) {
        linkService.removeFilter(chatId, id, filter);
    }


}
