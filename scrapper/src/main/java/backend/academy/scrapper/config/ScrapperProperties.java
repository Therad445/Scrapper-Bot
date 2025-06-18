package backend.academy.scrapper.config;

import jakarta.validation.constraints.NotEmpty;
import java.net.URI;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ScrapperProperties(
        @NotEmpty String githubToken,
        StackOverflowCredentials stackOverflow,
        URI botUrl,
        Scheduler scheduler,
        String accessType,
        MessageTransport messageTransport,
        KafkaProperties kafka) {
    public enum MessageTransport {
        HTTP,
        KAFKA
    }

    public record StackOverflowCredentials(@NotEmpty String key, @NotEmpty String accessToken) {}

    public record Scheduler(
            boolean enable, Duration interval, Duration forceCheckDelay, int batchSize, int threadCount) {}

    public record KafkaProperties(
            @NotEmpty String topic,
            @NotEmpty String dlqTopic,
            @NotEmpty String bootstrapServers,
            @NotEmpty String botToScrapperTopic) {}
}
