package backend.academy.scrapper;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;
import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
@EnableScheduling
@EnableTransactionManagement
public record ScrapperConfig(
    @NotEmpty String githubToken,
    StackOverflowCredentials stackOverflow,
    Scheduler scheduler
) {
    public record StackOverflowCredentials(@NotEmpty String key, @NotEmpty String accessToken) {}

    public record Scheduler(
        boolean enable,
        Duration interval,
        Duration forceCheckDelay,
        int batchSize
    ) {}

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
