package com.sro.SpringCoreTask1;


import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.sro.SpringCoreTask1.config.AppConfig;
import com.sro.SpringCoreTask1.dto.request.TrainerRequestDTO;
import com.sro.SpringCoreTask1.dto.request.TrainingTypeRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainerResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingTypeResponseDTO;
import com.sro.SpringCoreTask1.service.TrainerService;
import com.sro.SpringCoreTask1.service.TrainingTypeService;

public class SpringCoreTask1Application {

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        TrainingTypeRequestDTO trainingType = new TrainingTypeRequestDTO(
            "Strength"
        );

        TrainingTypeService trainingTypeService = context.getBean(TrainingTypeService.class);
        TrainingTypeResponseDTO savedTrainingType =  trainingTypeService.save(trainingType);
        

        TrainerRequestDTO trainer = new TrainerRequestDTO(
            "Sebas",
            "Restrepo",
            "sebas.restrepo",
            "p6q7r8s9t0",
            true,
            savedTrainingType.id()
        );

        // 🔹 Obtener el Bean como TrainerRepository (NO TrainerRepositoryImpl)
        TrainerService trainerService = context.getBean(TrainerService.class);
        TrainerResponseDTO savedTrainer = trainerService.save(trainer);

        context.close();

    }
}
