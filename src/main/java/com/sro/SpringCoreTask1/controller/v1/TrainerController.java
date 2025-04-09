package com.sro.SpringCoreTask1.controller.v1;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sro.SpringCoreTask1.dtos.v1.request.trainer.RegisterTrainerRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainer.UpdateTrainerProfileRequest;
import com.sro.SpringCoreTask1.dtos.v1.response.trainer.RegisterTrainerResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainer.TrainerProfileResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainer.UnassignedTrainerResponse;
import com.sro.SpringCoreTask1.service.TrainerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/api/v1/trainers", produces = "application/json")
public class TrainerController {

    private final TrainerService trainerService;

    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @PostMapping
    public ResponseEntity<RegisterTrainerResponse> registerTrainer(
            @Valid @RequestBody RegisterTrainerRequest trainerRequest) {
        RegisterTrainerResponse trainerResponse = trainerService.saveFromAuth(trainerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(trainerResponse);
    }

    @GetMapping("/{username}")
    public ResponseEntity<TrainerProfileResponse> getProfile(@PathVariable String username) {
        TrainerProfileResponse profile = trainerService.findByUsername(username);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{username}")
    public ResponseEntity<TrainerProfileResponse> updateProfile(
            @PathVariable String username,
            @Valid @RequestBody UpdateTrainerProfileRequest trainerUpdateRequestDTO) {
        TrainerProfileResponse profile = trainerService.update(username, trainerUpdateRequestDTO);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/unassigned")
    public ResponseEntity<List<UnassignedTrainerResponse>> getUnassignedTrainers(
            @RequestParam String traineeUsername) {
        List<UnassignedTrainerResponse> trainers = trainerService.findUnassignedTrainersByTraineeUsername(traineeUsername);
        return ResponseEntity.ok(trainers);
    }
}