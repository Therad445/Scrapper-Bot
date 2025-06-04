package backend.academy.scrapper.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

/**
 * Конфигурационный класс: здесь регистрируем бины, включаем аннотации @EnableScheduling и т.п.
 * Также «подключаем» ScrapperProperties для заполнения из application.yaml.
 */
@Configuration
@EnableScheduling
@EnableTransactionManagement
@EnableConfigurationProperties(ScrapperProperties.class)
public class ScrapperConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
