package backend.academy.scrapper.config;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ScrapperProperties(
    @NotEmpty String githubToken,
    StackOverflowCredentials stackOverflow,
    URI botUrl,
    Scheduler scheduler,
    String accessType,
    MessageTransport messageTransport,
    KafkaProperties kafka
) {
    public record StackOverflowCredentials(
        @NotEmpty String key,
        @NotEmpty String accessToken
    ) {}

    public record Scheduler(
        boolean enable,
        Duration interval,
        Duration forceCheckDelay,
        int batchSize,
        int threadCount
    ) {}

    public enum MessageTransport {
        HTTP,
        KAFKA
    }

    public record KafkaProperties(
        @NotEmpty String topic,
        @NotEmpty String dlqTopic,
        @NotEmpty String bootstrapServers
    ) {}
}
