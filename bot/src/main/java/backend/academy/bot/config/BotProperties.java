package backend.academy.bot.config;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import java.time.Duration;


@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public class BotProperties {

    @NotEmpty
    private String telegramToken;

    @NotEmpty
    private String scrapperUrl;

    @NotNull
    private Duration cacheTtl;

    private Kafka kafka = new Kafka();

    public String getTelegramToken() {
        return telegramToken;
    }

    public void setTelegramToken(String telegramToken) {
        this.telegramToken = telegramToken;
    }

    public String getScrapperUrl() {
        return scrapperUrl;
    }

    public void setScrapperUrl(String scrapperUrl) {
        this.scrapperUrl = scrapperUrl;
    }

    public Kafka getKafka() {
        return kafka;
    }

    public void setKafka(Kafka kafka) {
        this.kafka = kafka;
    }

    public Duration getCacheTtl() {
        return cacheTtl;
    }

    public void setCacheTtl(Duration cacheTtl) {
        this.cacheTtl = cacheTtl;
    }

    public static class Kafka {
        @NotEmpty
        private String bootstrapServers;

        @NotEmpty
        private String groupId;

        @NotEmpty
        private String topic;

        @NotEmpty
        private String dlqTopic;

        @NotEmpty
        private String commandsTopic;

        public String getCommandsTopic() { return commandsTopic; }

        public void setCommandsTopic(String commandsTopic) { this.commandsTopic = commandsTopic; }

        public String getBootstrapServers() {
            return bootstrapServers;
        }

        public void setBootstrapServers(String bootstrapServers) {
            this.bootstrapServers = bootstrapServers;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public String getDlqTopic() {
            return dlqTopic;
        }

        public void setDlqTopic(String dlqTopic) {
            this.dlqTopic = dlqTopic;
        }
    }
}
