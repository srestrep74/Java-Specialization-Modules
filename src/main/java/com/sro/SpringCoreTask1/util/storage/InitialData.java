package com.sro.SpringCoreTask1.util.storage;

import java.util.List;

import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.entity.Training;
import com.sro.SpringCoreTask1.entity.TrainingType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InitialData {
    private List<Trainer> trainers;
    private List<Trainee> trainees;
    private List<Training> trainings;
    private List<TrainingType> trainingTypes;
}
