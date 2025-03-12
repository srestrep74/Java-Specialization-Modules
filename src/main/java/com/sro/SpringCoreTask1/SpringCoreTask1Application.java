package com.sro.SpringCoreTask1;

import java.time.LocalDate;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.sro.SpringCoreTask1.config.AppConfig;
import com.sro.SpringCoreTask1.dto.request.TrainingRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingResponseDTO;
import com.sro.SpringCoreTask1.service.TrainingService;

public class SpringCoreTask1Application {

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);



        TrainingService trainingService = context.getBean(TrainingService.class);
        TrainingRequestDTO trainingRequestDTO = new TrainingRequestDTO("Training1", LocalDate.of(2021, 11, 13),2, 6L, 1L, 1L);

        TrainingResponseDTO trainingResponseDTO = trainingService.save(trainingRequestDTO);

        System.out.println(trainingResponseDTO);


        context.close();

    }
}
