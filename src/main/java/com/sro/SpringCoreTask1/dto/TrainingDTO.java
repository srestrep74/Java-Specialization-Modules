package com.sro.SpringCoreTask1.dto;

import java.time.Duration;
import java.time.LocalDate;

import com.sro.SpringCoreTask1.models.TrainingType;
import com.sro.SpringCoreTask1.models.id.TrainingId;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TrainingDTO {
    private TrainingId trainingId;
    private String trainingName;
    private LocalDate trainingDate;
    private Duration duration;
    private TraineeDTO trainee;
    private TrainerDTO trainer;
    private TrainingType trainingType;
}
