package backend.academy.scrapper.model;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

import java.util.Set;

public record LinkUpdate(
    @NotNull Long id,
    @URL @NotNull String url,
    @NotNull String description,
    @NotNull Set<Long> tgChatIds) {}
