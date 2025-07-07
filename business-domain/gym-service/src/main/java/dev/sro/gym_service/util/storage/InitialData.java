package dev.sro.gym_service.util.storage;

import java.util.List;

import dev.sro.gym_service.dtos.v1.request.seed.TraineeSeedRequest;
import dev.sro.gym_service.dtos.v1.request.seed.TrainerSeedRequest;
import dev.sro.gym_service.dtos.v1.request.seed.TrainingSeedRequest;
import dev.sro.gym_service.dtos.v1.request.seed.TrainingTypeSeedRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InitialData {
    private List<TrainerSeedRequest> trainers;
    private List<TraineeSeedRequest> trainees;
    private List<TrainingSeedRequest> trainings;
    private List<TrainingTypeSeedRequest> trainingTypes;
}
