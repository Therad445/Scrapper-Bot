package backend.academy.scrapper.model;

import jakarta.validation.constraints.NotNull;
import java.util.Set;
import org.hibernate.validator.constraints.URL;

public record LinkUpdate(
    @NotNull Long id,
    @URL @NotNull String url,
    @NotNull String description,
    @NotNull Set<Long> tgChatIds) {
}
