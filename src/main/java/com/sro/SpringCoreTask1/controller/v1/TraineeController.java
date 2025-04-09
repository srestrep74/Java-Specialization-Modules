package com.sro.SpringCoreTask1.controller.v1;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sro.SpringCoreTask1.dtos.v1.request.trainee.RegisterTraineeRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainee.UpdateTraineeActivation;
import com.sro.SpringCoreTask1.dtos.v1.request.trainee.UpdateTraineeProfileRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TraineeTrainingFilter;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TraineeTrainingResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.RegisterTraineeResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.TraineeProfileResponse;
import com.sro.SpringCoreTask1.service.TraineeService;
import com.sro.SpringCoreTask1.service.TrainingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/api/v1/trainees", produces = "application/json")
public class TraineeController {

    private final TraineeService traineeService;
    private final TrainingService trainingService;

    public TraineeController(TraineeService traineeService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainingService = trainingService;
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

    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TraineeTrainingResponse>> getTraineeTrainings(
            @PathVariable String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String trainerName,
            @RequestParam(required = false) String trainingType) {
        
        TraineeTrainingFilter filterDTO = new TraineeTrainingFilter(
            username, fromDate, toDate, trainerName, trainingType);
        
        List<TraineeTrainingResponse> trainings = trainingService.findTrainingsByTraineeWithFilters(filterDTO);
        return ResponseEntity.ok(trainings);
    }

    @PatchMapping("/{username}/activation")
    public ResponseEntity<Void> updateActivationStatus(
            @PathVariable String username,
            @RequestBody UpdateTraineeActivation updateTraineeActivation) {
        traineeService.updateActivationStatus(username, updateTraineeActivation.active());
        return ResponseEntity.noContent().build();
    }
}