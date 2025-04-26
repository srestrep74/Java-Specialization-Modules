package com.sro.SpringCoreTask1.health;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!local")
public class DatabaseHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    public DatabaseHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1000)) {
                return Health.up()
                        .withDetail("database", "PostgreSQL")
                        .withDetail("message", "Connection successful")
                        .build();
            }
        } catch (SQLException e) {
            return Health.down()
                    .withDetail("database", "PostgreSQL")
                    .withDetail("message", "Connection failed")
                    .withDetail("error", e)
                    .build();
        }
        return Health.down().withDetail("error", "Unable to validate connection to database").build();
    }

}
