package com.sro.SpringCoreTask1;

import java.time.LocalDate;
import java.util.List;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.sro.SpringCoreTask1.config.AppConfig;
import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.repository.TraineeRepository;
import com.sro.SpringCoreTask1.service.TraineeService;

public class SpringCoreTask1Application {

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        TraineeRepository traineeRepository = context.getBean(TraineeRepository.class);

        Trainee trainee = new Trainee();

        trainee.setFirstName("Santi");
        trainee.setLastName("Romeo");
        trainee.setUsername("SantiRomeo");
        trainee.setPassword("1234");
        trainee.setActive(true);
        trainee.setAddress("Calle 1");
        trainee.setDateOfBirth(LocalDate.of(2002, 1, 15));

        traineeRepository.save(trainee);

        System.out.println(traineeRepository.findByUsername("SantiRomeo").get().toString());

        //List<Trainee> trainees = traineeRepository.findAll();
        //trainees.forEach(traineee -> System.out.println(traineee.toString()));
    }
}
