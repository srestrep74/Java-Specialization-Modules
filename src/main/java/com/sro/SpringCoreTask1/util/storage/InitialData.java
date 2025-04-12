package com.sro.SpringCoreTask1.util.storage;

import java.util.List;

import com.sro.SpringCoreTask1.dtos.v1.request.seed.TraineeSeedRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.seed.TrainerSeedRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.seed.TrainingSeedRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.seed.TrainingTypeSeedRequest;

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
