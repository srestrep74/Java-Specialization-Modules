package com.sro.SpringCoreTask1.util.storage;

import java.util.List;

import com.sro.SpringCoreTask1.dto.request.TraineeRequestDTO;
import com.sro.SpringCoreTask1.dto.request.TrainerRequestDTO;
import com.sro.SpringCoreTask1.dto.request.TrainingRequestDTO;
import com.sro.SpringCoreTask1.dto.request.TrainingTypeRequestDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InitialData {
    private List<TrainerRequestDTO> trainers;
    private List<TraineeRequestDTO> trainees;
    private List<TrainingRequestDTO> trainings;
    private List<TrainingTypeRequestDTO> trainingTypes;
}
