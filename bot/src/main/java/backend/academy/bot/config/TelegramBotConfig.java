package backend.academy.bot.config;

import com.pengrad.telegrambot.TelegramBot;
import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TelegramBotConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public TelegramBot telegramBot(@Value("${app.telegram-token}") String token) {
        return new TelegramBot(token);
    }

    @Bean
    public URI urlTelegramBot(@Value("${app.scrapper-url}") URI url) {
        return url;
    }
}
