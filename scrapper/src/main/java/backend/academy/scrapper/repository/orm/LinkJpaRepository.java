package backend.academy.scrapper.repository.orm;

import backend.academy.scrapper.entity.LinkEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface LinkJpaRepository extends JpaRepository<LinkEntity, Long> {

    Optional<LinkEntity> findByUrl(String url);

    @Query("""
        SELECT l FROM LinkEntity l
         WHERE l.lastCheckedAt < :threshold
         ORDER BY l.lastCheckedAt
    """)
    Page<LinkEntity> findOldLinks(Instant threshold, Pageable pageable);
}
