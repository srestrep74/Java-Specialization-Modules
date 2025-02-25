package com.sro.SpringCoreTask1.service;

import com.sro.SpringCoreTask1.dao.TrainingDAO;
import com.sro.SpringCoreTask1.dto.TraineeDTO;
import com.sro.SpringCoreTask1.dto.TrainerDTO;
import com.sro.SpringCoreTask1.dto.TrainingDTO;
import com.sro.SpringCoreTask1.mappers.TrainingMapper;
import com.sro.SpringCoreTask1.models.Trainee;
import com.sro.SpringCoreTask1.models.Trainer;
import com.sro.SpringCoreTask1.models.Training;
import com.sro.SpringCoreTask1.models.id.TrainingId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TrainingServiceTest {

    @Mock
    private TrainingDAO trainingDAO;

    @InjectMocks
    private TrainingService trainingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveTraining() {
        TrainingDTO trainingDTO = createTrainingDTO();
        Training training = TrainingMapper.toEntity(trainingDTO);

        when(trainingDAO.save(any(Training.class))).thenReturn(training);

        TrainingDTO savedTraining = trainingService.save(trainingDTO);

        assertNotNull(savedTraining);
        assertEquals("Yoga Class", savedTraining.getTrainingName());
        verify(trainingDAO, times(1)).save(any(Training.class));
    }

    @Test
    void testFindTrainingById() {
        TrainingId trainingId = new TrainingId(1L, 1L);
        Training training = createTraining(trainingId);

        when(trainingDAO.findById(trainingId)).thenReturn(Optional.of(training));

        TrainingDTO foundTraining = trainingService.findById(trainingId);

        assertNotNull(foundTraining);
        assertEquals("Yoga Class", foundTraining.getTrainingName());
    }

    @Test
    void testFindAllTrainings() {
        Training training = createTraining(new TrainingId(1L, 1L));

        when(trainingDAO.findAll()).thenReturn(List.of(training));

        List<TrainingDTO> trainings = trainingService.findAll();

        assertNotNull(trainings);
        assertEquals(1, trainings.size());
        assertEquals("Yoga Class", trainings.get(0).getTrainingName());
    }

    @Test
    void testDeleteTraining() {
        TrainingId trainingId = new TrainingId(1L, 1L);

        doNothing().when(trainingDAO).delete(eq(trainingId));

        trainingService.delete(trainingId);

        verify(trainingDAO, times(1)).delete(eq(trainingId));
    }

    private TrainingDTO createTrainingDTO() {
        TrainingDTO trainingDTO = new TrainingDTO();
        trainingDTO.setTrainingName("Yoga Class");
        trainingDTO.setTrainingDate(LocalDate.of(2023, 10, 1));
        trainingDTO.setDuration(Duration.ofHours(1));

        TraineeDTO traineeDTO = new TraineeDTO();
        traineeDTO.setUserId(1L);
        trainingDTO.setTrainee(traineeDTO);

        TrainerDTO trainerDTO = new TrainerDTO();
        trainerDTO.setUserId(2L);
        trainingDTO.setTrainer(trainerDTO);

        return trainingDTO;
    }

    private Training createTraining(TrainingId trainingId) {
        Trainee trainee = new Trainee();
        trainee.setUserId(1L);

        Trainer trainer = new Trainer();
        trainer.setUserId(1L);

        Training training = new Training();
        training.setTrainingId(trainingId);
        training.setTrainingName("Yoga Class");
        training.setTrainee(trainee);
        training.setTrainer(trainer);

        return training;
    }
}