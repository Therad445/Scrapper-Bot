package backend.academy.scrapper.controller;

import backend.academy.scrapper.service.ChatService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/tg-chat")
@Validated
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> registerChat(@PathVariable @NotNull @Positive Long id) {
        chatService.register(id);
        log.info("Чат {} зарегистрирован", id);
        return ResponseEntity.ok()
            .header("Content-Type", "application/json;charset=UTF-8")
            .body("Чат зарегистрирован");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChat(@PathVariable @NotNull @Positive Long id) {
        chatService.delete(id);
        log.info("Чат {} успешно удалён", id);
        return ResponseEntity.ok()
            .header("Content-Type", "application/json;charset=UTF-8")
            .body("Чат успешно удалён");
    }

}
