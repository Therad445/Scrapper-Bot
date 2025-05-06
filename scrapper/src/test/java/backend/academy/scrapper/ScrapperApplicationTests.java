package backend.academy.scrapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestTemplate;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ScrapperApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
    }

    @Test
    void restTemplateBeanIsPresent() {
        assertThat(applicationContext.getBean(RestTemplate.class)).isNotNull();
    }

    @Test
    void scrapperConfigBeanIsPresent() {
        assertThat(applicationContext.getBean(ScrapperConfig.class)).isNotNull();
    }
}
