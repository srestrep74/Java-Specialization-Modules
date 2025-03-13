package com.sro.SpringCoreTask1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@Configuration
public class PersistenceConfig {
    @Bean
    public EntityManagerFactory entityManagerFactory(){
        return Persistence.createEntityManagerFactory("myUnit");
    }

    @Bean
    public EntityManager entityManager(EntityManagerFactory emf){
        return emf.createEntityManager();
    }
}
