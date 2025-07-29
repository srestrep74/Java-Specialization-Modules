package dev.sro.workload_service.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

@TestConfiguration
public class TestMongoConfig {

    @Bean
    @Primary
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb://admin:admin123@localhost:27017/admin");
    }

    @Bean
    @Primary
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("workload_test_db");
        return new MongoTemplate(mongoClient, database.getName());
    }

    @Bean
    public MongoMappingContext mongoMappingContext() {
        return new MongoMappingContext();
    }
} 