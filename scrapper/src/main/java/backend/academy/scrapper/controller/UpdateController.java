package backend.academy.scrapper.controller;

import backend.academy.scrapper.model.LinkUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/updates")
public class UpdateController {

    private final NotificationService notificationService;

    @Autowired
    public UpdateController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<Void> sendUpdate(@RequestBody LinkUpdate linkUpdate) {
        notificationService.sendUpdate(linkUpdate);
        return ResponseEntity.ok().build();
    }
}
