package backend.academy.scrapper.repository.orm;

import backend.academy.scrapper.entity.ChatEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatJpaRepository extends JpaRepository<ChatEntity, Long> {

    @Query(
            """
            select c.id
              from ChatEntity         c
              join c.subscriptions    s
             where s.link.id = :linkId
        """)
    List<Long> findChatIdsByLinkId(@Param("linkId") long linkId);
}
