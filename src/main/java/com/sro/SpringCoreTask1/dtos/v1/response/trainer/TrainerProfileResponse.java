package com.sro.SpringCoreTask1.dtos.v1.response.trainer;

import java.util.List;

public record TrainerProfileResponse(
    String firstName,
    String lastName,
    Long specialization,
    boolean active,
    List<TraineeInfo> trainees
) {
    public record TraineeInfo(
        String username,
        String firstName,
        String lastName
    ) {}
}
