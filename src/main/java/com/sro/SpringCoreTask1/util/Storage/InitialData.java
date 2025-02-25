package com.sro.SpringCoreTask1.util.storage;

import java.util.List;

import com.sro.SpringCoreTask1.dto.TraineeDTO;
import com.sro.SpringCoreTask1.dto.TrainerDTO;
import com.sro.SpringCoreTask1.dto.TrainingDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InitialData {
    private List<TrainerDTO> trainers;
    private List<TraineeDTO> trainees;
    private List<TrainingDTO> trainings;
}
