package com.sro.SpringCoreTask1.controller;

import com.sro.SpringCoreTask1.dtos.v1.response.trainingType.TrainingTypeResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TrainingTypeControllerWebTestClientTest {

    private static final String BASE_URL = "/api/v1/training-types";

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldReturnListOfTrainingTypes() {
        webTestClient.get()
                .uri(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TrainingTypeResponse.class)
                .value(types -> {
                    assertNotNull(types);
                    assertFalse(types.isEmpty());
                });
    }
}