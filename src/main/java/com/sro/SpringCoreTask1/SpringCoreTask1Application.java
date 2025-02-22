package com.sro.SpringCoreTask1;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.sro.SpringCoreTask1.config.AppConfig;
import com.sro.SpringCoreTask1.service.TraineeService;

public class SpringCoreTask1Application {

	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

		TraineeService traineeService = context.getBean(TraineeService.class);

		CommandLineRunner runner = context.getBean(CommandLineRunner.class);
		try {
			runner.run(args);
		}catch (Exception e) {
			e.printStackTrace();
		}

		traineeService.findAll().forEach(trainee -> System.out.println(trainee.getFirstName()));
	}
}
