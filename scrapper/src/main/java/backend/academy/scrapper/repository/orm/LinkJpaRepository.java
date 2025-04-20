package backend.academy.scrapper.repository.orm;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkJpaRepository extends JpaRepository<LinkEntity, Long> {
    List<LinkEntity> findByChatId(Long chatId);
    Optional<LinkEntity> findByChatIdAndLink(Long chatId, String link);
    void deleteByChatIdAndLink(Long chatId, String link);
}
