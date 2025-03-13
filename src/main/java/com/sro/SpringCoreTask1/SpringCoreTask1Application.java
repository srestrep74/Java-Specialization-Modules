package com.sro.SpringCoreTask1;

import java.time.LocalDate;
import java.util.List;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.sro.SpringCoreTask1.config.AppConfig;
import com.sro.SpringCoreTask1.dto.TraineeTrainingFilterDTO;
import com.sro.SpringCoreTask1.dto.TrainerTrainingFilterDTO;
import com.sro.SpringCoreTask1.dto.request.TrainingRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainerResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingResponseDTO;
import com.sro.SpringCoreTask1.service.TrainerService;
import com.sro.SpringCoreTask1.service.TrainingService;

public class SpringCoreTask1Application {

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        TrainingService trainingService = context.getBean(TrainingService.class);

        TrainerService trainerService = context.getBean(TrainerService.class);
        
        List<TrainerResponseDTO> trainers = trainerService.getTrainersNotAssignedToTrainee("trainee1");

        trainers.forEach(trainer -> System.out.println(trainer));
        context.close();

    }
}
