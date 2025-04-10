package com.sro.SpringCoreTask1.dtos.v1.request.trainee;

import java.util.List;

public record UpdateTraineeTrainerListRequest(
    List<String> trainers
) {}
