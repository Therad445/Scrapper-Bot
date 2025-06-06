package backend.academy.scrapper.kafka;


import java.util.Set;

import backend.academy.scrapper.kafka.command.TrackCommand;
import backend.academy.scrapper.kafka.command.UntrackCommand;
import backend.academy.scrapper.kafka.command.ListCommand;
import backend.academy.scrapper.model.AddLinkRequest;
import backend.academy.scrapper.model.RemoveLinkRequest;
import backend.academy.scrapper.model.ListLinksResponse;
import backend.academy.scrapper.service.LinkService;
import backend.academy.scrapper.service.ChatService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
@ConditionalOnProperty(prefix = "app", name = "message-transport", havingValue = "kafka")
public class ScrapperCommandsListener {

    private static final Logger log = LoggerFactory.getLogger(ScrapperCommandsListener.class);

    private final LinkService linkService;
    private final ChatService chatService;

    @Value("${app.kafka.bot-to-scrapper-topic}")
    private String commandsTopic;

    @Value("${app.kafka.dlq-topic}")
    private String dlqTopic;

    public ScrapperCommandsListener(LinkService linkService, ChatService chatService) {
        this.linkService = linkService;
        this.chatService = chatService;
    }


    @KafkaListener(
        topics = "${app.kafka.bot-to-scrapper-topic}",
        groupId = "scrapper-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void onTrackCommand(TrackCommand cmd) {
        try {
            log.info("Получена команда TrackCommand: {}", cmd);

            chatService.register(cmd.getChatId());

            AddLinkRequest req = new AddLinkRequest(
                cmd.getLink().toString(),
                cmd.getTags() != null ? Set.copyOf(cmd.getTags()) : Set.of(),
                cmd.getFilters() != null ? Set.copyOf(cmd.getFilters()) : Set.of()
            );

            linkService.addLink(cmd.getChatId(), req);

            log.info("TrackCommand успешно обработан для chatId={}", cmd.getChatId());
        } catch (Exception e) {
            log.error("Ошибка при обработке TrackCommand: {}", e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(
        topics = "${app.kafka.bot-to-scrapper-topic}",
        groupId = "scrapper-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void onUntrackCommand(UntrackCommand cmd) {
        try {
            log.info("Получена команда UntrackCommand: {}", cmd);

            RemoveLinkRequest req = new RemoveLinkRequest(cmd.getLink().toString());
            linkService.removeLinks(cmd.getChatId(), req);

            log.info("UntrackCommand успешно обработан для chatId={}", cmd.getChatId());
        } catch (Exception e) {
            log.error("Ошибка при обработке UntrackCommand: {}", e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(
        topics = "${app.kafka.bot-to-scrapper-topic}",
        groupId = "scrapper-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void onListCommand(ListCommand cmd) {
        try {
            log.info("Получена команда ListCommand: {}", cmd);

            ListLinksResponse response = linkService.getLinks(cmd.getChatId());

            log.info("Список ссылок для chatId={} (size={}): {}",
                cmd.getChatId(),
                response.size(),
                response.links());

        } catch (Exception e) {
            log.error("Ошибка при обработке ListCommand: {}", e.getMessage(), e);
            throw e;
        }
    }
}
