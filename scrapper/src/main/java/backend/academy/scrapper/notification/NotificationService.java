package backend.academy.scrapper.notification;

import backend.academy.scrapper.model.LinkUpdate;

public interface NotificationService {
    void notify(LinkUpdate update);
}
