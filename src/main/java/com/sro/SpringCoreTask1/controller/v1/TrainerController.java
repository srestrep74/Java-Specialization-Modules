package com.sro.SpringCoreTask1.controller.v1;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sro.SpringCoreTask1.dtos.v1.request.trainer.RegisterTrainerRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainer.UpdateTrainerProfileRequest;
import com.sro.SpringCoreTask1.dtos.v1.response.trainer.RegisterTrainerResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainer.TrainerProfileResponse;
import com.sro.SpringCoreTask1.service.TrainerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/api/v1/trainers", produces = "application/json")
public class TrainerController {
    
    private final TrainerService trainerService;

    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterTrainerResponse> registerTrainer(
        @Valid @RequestBody RegisterTrainerRequest trainerRequest
    ) {
        RegisterTrainerResponse trainerResponse = trainerService.saveFromAuth(trainerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(trainerResponse);
    }
    
    @GetMapping("/{username}")
    public ResponseEntity<TrainerProfileResponse> getProfile(@PathVariable String username) {
        TrainerProfileResponse profile = trainerService.findByUsername(username);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{username}")
    public ResponseEntity<TrainerProfileResponse> updateProfile(@PathVariable String username, @RequestBody UpdateTrainerProfileRequest trainerUpdateRequestDTO) {
        TrainerProfileResponse profile = trainerService.update(username, trainerUpdateRequestDTO);
        return ResponseEntity.ok(profile);
    }

}
