package com.sro.SpringCoreTask1;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.sro.SpringCoreTask1.config.AppConfig;
import com.sro.SpringCoreTask1.service.TraineeService;

public class SpringCoreTask1Application {

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        TraineeService traineeService = context.getBean(TraineeService.class);

        traineeService.addTrainerToTrainee(1L, 7L);
        traineeService.removeTrainerFromTrainee(1L, 6L);
    }
}
