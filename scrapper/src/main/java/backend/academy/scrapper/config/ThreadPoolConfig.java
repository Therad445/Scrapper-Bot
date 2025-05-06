package backend.academy.scrapper.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThreadPoolConfig {

    @Bean(name = "linkCheckerPool", destroyMethod = "shutdown")
    public ExecutorService linkCheckerPool(ScrapperConfig cfg) {
        int n = Math.max(1, cfg.scheduler().threadCount());
        return Executors.newFixedThreadPool(n, r -> new Thread(r, "link-checker-" + r.hashCode()));
    }
}
