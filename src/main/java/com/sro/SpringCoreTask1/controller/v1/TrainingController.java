package com.sro.SpringCoreTask1.controller.v1;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sro.SpringCoreTask1.dtos.v1.request.training.CreateTrainingRequest;
import com.sro.SpringCoreTask1.service.TrainingService;

@RestController
@RequestMapping(value = "/api/v1/trainings", produces = "application/json")
public class TrainingController {
    
    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping
    public ResponseEntity<Void> addTraining(@RequestBody CreateTrainingRequest createTrainingRequest) {
        trainingService.addTraining(createTrainingRequest);
        return ResponseEntity.ok().build();
    }
    
}
