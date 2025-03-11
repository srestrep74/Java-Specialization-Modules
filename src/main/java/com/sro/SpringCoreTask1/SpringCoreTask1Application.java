package com.sro.SpringCoreTask1;


import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.sro.SpringCoreTask1.config.AppConfig;
import com.sro.SpringCoreTask1.dto.response.TrainerResponseDTO;
import com.sro.SpringCoreTask1.service.TrainerService;

public class SpringCoreTask1Application {

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        TrainerService trainerService = context.getBean(TrainerService.class);

        TrainerResponseDTO trainerResponseDTO = trainerService.findByUsername("sebas.restrepo").get();
        System.out.println(trainerResponseDTO);

        context.close();

    }
}
