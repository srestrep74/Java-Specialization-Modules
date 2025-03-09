package com.sro.SpringCoreTask1;

import java.util.Arrays;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.sro.SpringCoreTask1.config.AppConfig;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.entity.TrainingType;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.repository.TrainingTypeRepository;

public class SpringCoreTask1Application {

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        Arrays.stream(context.getBeanDefinitionNames()).forEach(System.out::println);

        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName("Strength");

        TrainingTypeRepository trainingTypeRepository = context.getBean(TrainingTypeRepository.class);
        trainingTypeRepository.save(trainingType);
        

        Trainer trainer = new Trainer();
        trainer.setFirstName("Bob");
        trainer.setLastName("Brown");
        trainer.setUsername("bob.brown");
        trainer.setPassword("p6q7r8s9t0");
        trainer.setActive(true);
        trainer.setTrainingType(trainingType);

        // 🔹 Obtener el Bean como TrainerRepository (NO TrainerRepositoryImpl)
        TrainerRepository trainerRepository = context.getBean(TrainerRepository.class);
        trainerRepository.save(trainer);
    }
}
