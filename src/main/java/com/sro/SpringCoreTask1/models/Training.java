package com.sro.SpringCoreTask1.models;

import java.time.Duration;
import java.time.LocalDate;

import com.sro.SpringCoreTask1.models.id.TrainingId;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Training {
    
    private TrainingId trainingId;
    private String trainingName;
    private LocalDate trainingDate;
    private Duration duration;
    private Trainee trainee;
    private Trainer trainer;
    private TrainingType trainingType;
}
