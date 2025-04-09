package com.sro.SpringCoreTask1.controller.v1;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sro.SpringCoreTask1.dtos.v1.response.trainingType.TrainingTypeResponse;
import com.sro.SpringCoreTask1.service.TrainingTypeService;

@RestController
@RequestMapping(value = "/api/v1/training-types", produces = "application/json")
public class TrainingTypeController {
    
    private final TrainingTypeService trainingTypeService;

    public TrainingTypeController(TrainingTypeService trainingTypeService) {
        this.trainingTypeService = trainingTypeService;
    }
    
    @GetMapping
    public ResponseEntity<List<TrainingTypeResponse>> findAll() {
        return ResponseEntity.ok(trainingTypeService.findAll());
    }
    
}
