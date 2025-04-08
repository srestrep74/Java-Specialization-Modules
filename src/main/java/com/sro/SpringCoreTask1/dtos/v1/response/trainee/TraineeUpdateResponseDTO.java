package com.sro.SpringCoreTask1.dtos.v1.response.trainee;

import java.time.LocalDate;
import java.util.List;

public record TraineeUpdateResponseDTO(
    String username,
    String firstName,
    String lastName,
    LocalDate dateOfBirth,
    String address,
    boolean active,
    List<TrainerSummaryResponseDTO> trainers
) {}
