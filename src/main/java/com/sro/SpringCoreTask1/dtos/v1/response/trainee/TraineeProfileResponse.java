package com.sro.SpringCoreTask1.dtos.v1.response.trainee;

import java.time.LocalDate;
import java.util.List;

public record TraineeProfileResponse(
    String firstName,
    String lastName,
    LocalDate dateOfBirth,
    String address,
    boolean active,
    List<TrainerInfo> trainers
) {
    public record TrainerInfo(
        String username,
        String firstName,
        String lastName,
        Long specialization
    ) {}
}
