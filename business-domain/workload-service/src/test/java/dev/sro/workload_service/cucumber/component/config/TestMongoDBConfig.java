package dev.sro.workload_service.cucumber.component.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@TestConfiguration
@TestExecutionListeners({
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class
})
public class TestMongoDBConfig {

    private final MongoTemplate mongoTemplate;

    public TestMongoDBConfig(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void cleanDatabaseOnStartup() {
        // Clean all collections on startup
        mongoTemplate.getDb().drop();
    }

    @PreDestroy
    public void cleanDatabaseOnShutdown() {
        // Clean all collections on shutdown
        mongoTemplate.getDb().drop();
    }
}
