package backend.academy.scrapper.service;

import backend.academy.scrapper.model.LinkInfo;
import java.time.Instant;

public interface LinkChecker {
    boolean supports(LinkInfo link);

    boolean hasUpdates(LinkInfo link);

    String preview();

    Instant remoteUpdatedAt();
}
