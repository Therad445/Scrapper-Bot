package backend.academy.scrapper.repository.orm;

import backend.academy.scrapper.entity.TagEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagJpaRepository extends JpaRepository<TagEntity, Long> {
    Optional<TagEntity> findByName(String name);
}
