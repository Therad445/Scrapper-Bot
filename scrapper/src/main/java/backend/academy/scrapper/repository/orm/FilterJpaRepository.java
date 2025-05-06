package backend.academy.scrapper.repository.orm;

import backend.academy.scrapper.entity.FilterEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilterJpaRepository extends JpaRepository<FilterEntity, Long> {
    Optional<FilterEntity> findByName(String name);
}
