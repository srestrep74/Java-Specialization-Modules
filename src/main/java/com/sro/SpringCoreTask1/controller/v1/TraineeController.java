package com.sro.SpringCoreTask1.controller.v1;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sro.SpringCoreTask1.dtos.v1.request.trainee.RegisterTraineeRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainee.UpdateTraineeProfileRequest;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.RegisterTraineeResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.TraineeProfileResponse;
import com.sro.SpringCoreTask1.service.TraineeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/api/v1/trainees", produces = "application/json")
public class TraineeController {

    private final TraineeService traineeService;

    public TraineeController(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    @PostMapping
    public ResponseEntity<RegisterTraineeResponse> registerTrainee(
            @Valid @RequestBody RegisterTraineeRequest traineeRequest) {
        RegisterTraineeResponse traineeResponse = traineeService.saveFromAuth(traineeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(traineeResponse);
    }

    @GetMapping("/{username}")
    public ResponseEntity<TraineeProfileResponse> getProfile(@PathVariable String username) {
        TraineeProfileResponse profile = traineeService.findByUsername(username);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{username}")
    public ResponseEntity<TraineeProfileResponse> updateProfile(
            @PathVariable String username,
            @Valid @RequestBody UpdateTraineeProfileRequest traineeUpdateRequestDTO) {
        TraineeProfileResponse profile = traineeService.update(username, traineeUpdateRequestDTO);
        return ResponseEntity.ok(profile);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteProfile(@PathVariable String username) {
        traineeService.deleteByUsername(username);
        return ResponseEntity.noContent().build();
    }
}