package com.sro.SpringCoreTask1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class SpringCoreTask1Application {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(SpringCoreTask1Application.class, args);
    }
}
