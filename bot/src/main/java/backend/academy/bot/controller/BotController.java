package backend.academy.bot.controller;

import backend.academy.bot.dto.LinkUpdate;
import backend.academy.bot.service.MessageSenderService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@Slf4j
public class BotController {

    private final MessageSenderService sender;

    public BotController(MessageSenderService sender) {
        this.sender = sender;
    }

    @PostMapping("/updates")
    public ResponseEntity<?> sendNotification(@RequestBody @Valid LinkUpdate upd) {
        String message = "Обновилась ссылка: " + upd.getUrl() + "\n" +
            "Описание: " + upd.getDescription();
        upd.getTgChatIds().forEach(id -> sender.send(id, message));
        log.info("Обновление обработано");
        return ResponseEntity.ok("Обновление обработано");
    }
}

