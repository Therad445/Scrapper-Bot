package backend.academy.scrapper.repository;

import java.util.List;

public interface ChatRepository {
    void register(long chatId);

    void delete(long chatId);

    boolean exists(long chatId);

    List<Long> findChatIdsByLinkId(long linkId);
}
