package com.sro.SpringCoreTask1.controller;

import com.sro.SpringCoreTask1.dtos.v1.request.training.CreateTrainingRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainer.RegisterTrainerRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainee.RegisterTraineeRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainingType.TrainingTypeRequestDTO;
import com.sro.SpringCoreTask1.service.TraineeService;
import com.sro.SpringCoreTask1.service.TrainerService;
import com.sro.SpringCoreTask1.service.TrainingTypeService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TrainingControllerWebTestClientTest {

    private static final String BASE_URL = "/api/v1/trainings";
    private static String traineeUsername;
    private static String trainerUsername;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeAll
    static void setupAll(
            @Autowired TrainingTypeService trainingTypeService,
            @Autowired TraineeService traineeService,
            @Autowired TrainerService trainerService) {
        
        TrainingTypeRequestDTO typeRequest = new TrainingTypeRequestDTO("Strength Training");
        trainingTypeService.save(typeRequest);

        RegisterTraineeRequest traineeRequest = new RegisterTraineeRequest(
                "Test", "Trainee", LocalDate.of(1990, 1, 1), "Test Address");
        traineeUsername = traineeService.save(traineeRequest).username();

        RegisterTrainerRequest trainerRequest = new RegisterTrainerRequest(
                "Test", "Trainer", 1L);
        trainerUsername = trainerService.save(trainerRequest).username();
    }

    @Test
    @Order(1)
    void createTraining_ShouldReturnSuccess() {
        CreateTrainingRequest request = new CreateTrainingRequest(
                traineeUsername,
                trainerUsername,
                "Strength Training",
                LocalDate.now(),
                60
        );

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @Order(2)
    void createTraining_WithInvalidData_ShouldReturnBadRequest() {
        CreateTrainingRequest invalidRequest = new CreateTrainingRequest(
                "", "", "", null, 0);

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(3)
    void createTraining_WithNonExistentUsers_ShouldReturnNotFound() {
        CreateTrainingRequest request = new CreateTrainingRequest(
                "nonexistent.trainee",
                "nonexistent.trainer",
                "Invalid Training",
                LocalDate.now(),
                30
        );

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound();
    }
}