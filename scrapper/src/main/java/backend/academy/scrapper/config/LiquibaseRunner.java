package backend.academy.scrapper.config;

import jakarta.annotation.PostConstruct;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.changelog.ChangeSet;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.DirectoryResourceAccessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class LiquibaseRunner {

    private final DataSource dataSource;

    @PostConstruct
    public void runMigrations() {
        try (Connection connection = dataSource.getConnection()) {
            Database database = DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Path migrationsDir = Path.of(System.getProperty("user.dir"), "migrations");
            File changelog = migrationsDir.resolve("master.xml").toFile();

            Liquibase liquibase = new Liquibase(
                changelog.getName(),
                new DirectoryResourceAccessor(migrationsDir.toFile()),
                database
            );

            List<ChangeSet> pending = liquibase.listUnrunChangeSets(
                new Contexts(), new LabelExpression()
            );

            if (pending.isEmpty()) {
                log.info("Liquibase: все миграции уже применены");
            } else {
                log.info("Liquibase: найдено {} неприменённых миграций", pending.size());
                liquibase.update(new Contexts(), new LabelExpression());
                log.info("Liquibase: миграции успешно применены");
            }

        } catch (Exception e) {
            log.error("Liquibase: ошибка при запуске миграций", e);
            throw new IllegalStateException("Liquibase migration failed", e);
        }
    }
}
