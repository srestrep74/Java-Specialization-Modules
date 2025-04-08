package com.sro.SpringCoreTask1.controller.v1;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sro.SpringCoreTask1.dtos.v1.request.auth.TraineeRegistrationRequest;
import com.sro.SpringCoreTask1.dtos.v1.response.auth.TraineeRegistrationResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.TraineeProfileResponseDTO;
import com.sro.SpringCoreTask1.service.TraineeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/api/v1/trainees", produces = "application/json")
public class TraineeController {
    
    private final TraineeService traineeService;

    public TraineeController(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    @PostMapping("/register")
    public ResponseEntity<TraineeRegistrationResponse> registerTrainee(
        @Valid @RequestBody TraineeRegistrationRequest traineeRequest
    ) {
        TraineeRegistrationResponse traineeResponse = traineeService.saveFromAuth(traineeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(traineeResponse);
    }

    @GetMapping("/{username}")
    public ResponseEntity<TraineeProfileResponseDTO> getProfile(@PathVariable String username) {
        TraineeProfileResponseDTO profile = traineeService.findByUsername(username);
        return ResponseEntity.ok(profile);
    }
}
