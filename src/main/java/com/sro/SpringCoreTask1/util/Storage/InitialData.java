package com.sro.SpringCoreTask1.util.Storage;

import java.util.List;

import com.sro.SpringCoreTask1.models.Trainee;
import com.sro.SpringCoreTask1.models.Trainer;
import com.sro.SpringCoreTask1.models.Training;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InitialData {
    private List<Trainer> trainers;
    private List<Trainee> trainees;
    private List<Training> trainings;
}
