package backend.academy.scrapper.repository.orm;

import backend.academy.scrapper.entity.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatJpaRepository extends JpaRepository<ChatEntity, Long> {

    @Query("""
        select c.id
          from ChatEntity c
          join c.links l
         where l.id = :linkId
    """)
    List<Long> findChatIdsByLinkId(@Param("linkId") long linkId);
}
