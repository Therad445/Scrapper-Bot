package backend.academy.scrapper.service;

import backend.academy.scrapper.model.LinkInfo;

public interface LinkChecker {
    boolean hasUpdates(LinkInfo linkInfo);
    String  preview();
}
