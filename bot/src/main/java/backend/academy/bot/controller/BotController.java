package backend.academy.bot.controller;

import backend.academy.bot.dto.LinkUpdate;
import backend.academy.bot.telegram.TelegramBotService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@Slf4j
public class BotController {
    private final TelegramBotService telegramBotService;

    @Autowired
    public BotController(TelegramBotService telegramBotService) {
        this.telegramBotService = telegramBotService;
    }

    @PostMapping("/updates")
    public ResponseEntity<?> sendNotification(@RequestBody @Valid LinkUpdate linkUpdate) {
        telegramBotService.sendMessage(linkUpdate.getId(), "Обновилась ссылка " + linkUpdate.getUrl());
        log.info("Обновление обработано");
        return ResponseEntity.ok()
                .header("Content-Type", "application/json;charset=UTF-8")
                .body("Обновление обработано");
    }
}
