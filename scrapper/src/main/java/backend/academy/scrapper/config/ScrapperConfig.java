package backend.academy.scrapper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableScheduling
@EnableTransactionManagement
public class ScrapperConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
