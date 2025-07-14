package dev.sro.gym_service.dtos.v1.request.training;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record DeleteTrainingRequest(
        @NotBlank(message = "Trainee username cannot be blank") String traineeUsername,
        @NotBlank(message = "Trainer username cannot be blank") String trainerUsername,
        @NotNull(message = "Training date cannot be null")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate trainingDate
) {
} 