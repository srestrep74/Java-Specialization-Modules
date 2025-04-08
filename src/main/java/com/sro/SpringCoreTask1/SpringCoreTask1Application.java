package com.sro.SpringCoreTask1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.sro.SpringCoreTask1.config.AppConfig;
import com.sro.SpringCoreTask1.service.TraineeService;

/* 
@SpringBootApplication(exclude = {
    org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
    org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
    org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration.class
})*/
public class SpringCoreTask1Application {

    public static void main(String[] args) throws Exception {
        //SpringApplication.run(SpringCoreTask1Application.class, args);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        TraineeService traineeService = context.getBean(TraineeService.class);

        traineeService.deleteByUsername(traineeService.findById(6L).username());
    }
}
