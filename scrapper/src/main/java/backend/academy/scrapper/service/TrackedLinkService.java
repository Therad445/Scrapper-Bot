package backend.academy.scrapper.service;


import backend.academy.scrapper.model.TrackedLink;
import backend.academy.scrapper.repository.TrackedLinkRepository;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.List;

/**
 * Логика управления ссылками (добавление, удаление, получение).
 */
@Service
public class TrackedLinkService {
    private final TrackedLinkRepository trackedLinkRepository;

    public TrackedLinkService(TrackedLinkRepository trackedLinkRepository) {
        this.trackedLinkRepository = trackedLinkRepository;
    }

    public List<TrackedLink> getAllTrackedLinks() {
        return trackedLinkRepository.findAll();
    }

    public void saveTrackedLink(TrackedLink trackedLink) {
        trackedLinkRepository.save(trackedLink);
    }

    public void updateTrackedLink(TrackedLink trackedLink) {
        trackedLinkRepository.update(trackedLink);
    }

    public void deleteTrackedLink(String url) {
        trackedLinkRepository.delete(url);
    }

    // Инициализация демо-ссылки при запуске
    @PostConstruct
    public void init() {
        if (trackedLinkRepository.findAll().isEmpty()) {
            TrackedLink demoLink = new TrackedLink("https://example.com", "initial");
            trackedLinkRepository.save(demoLink);
        }
    }
}
