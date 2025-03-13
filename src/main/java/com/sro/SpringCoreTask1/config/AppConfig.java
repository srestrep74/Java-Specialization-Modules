package com.sro.SpringCoreTask1.config;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@ComponentScan(basePackages = "com.sro.SpringCoreTask1")
@Import({PersistenceConfig.class})
@EnableTransactionManagement
public class AppConfig {
}
