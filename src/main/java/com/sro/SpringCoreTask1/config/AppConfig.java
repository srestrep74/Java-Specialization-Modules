package com.sro.SpringCoreTask1.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = "com.sro.SpringCoreTask1")
@PropertySource("classpath:application.properties")
public class AppConfig {
    
}
