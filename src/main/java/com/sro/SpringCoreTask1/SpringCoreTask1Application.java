package com.sro.SpringCoreTask1;

import java.time.LocalDate;
import java.util.List;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.sro.SpringCoreTask1.config.AppConfig;
import com.sro.SpringCoreTask1.dto.TrainingFilterDTO;
import com.sro.SpringCoreTask1.dto.request.TrainingRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingResponseDTO;
import com.sro.SpringCoreTask1.service.TrainingService;

public class SpringCoreTask1Application {

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        TrainingService trainingService = context.getBean(TrainingService.class);
        
        TrainingFilterDTO trainingFilterDTO = new TrainingFilterDTO(6L, null, null, "sebas.brown", null);

        List<TrainingResponseDTO> trainings = trainingService.findTrainingsByFilters(trainingFilterDTO);

        trainings.forEach(training -> System.out.println(training));


        context.close();

    }
}
