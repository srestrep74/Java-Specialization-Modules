package com.sro.SpringCoreTask1.service;

import com.sro.SpringCoreTask1.dao.TrainerDAO;
import com.sro.SpringCoreTask1.dto.TrainerDTO;
import com.sro.SpringCoreTask1.mappers.TrainerMapper;
import com.sro.SpringCoreTask1.models.Trainer;
import com.sro.SpringCoreTask1.util.ProfileUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TrainerServiceTest {

    @Mock
    private TrainerDAO trainerDAO;

    @InjectMocks
    private TrainerService trainerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveTrainer() {
        TrainerDTO trainerDTO = createTrainerDTO();
        Trainer trainer = TrainerMapper.toEntity(trainerDTO);
        trainer.setUserName("john.doe");
        trainer.setPassword("a1b2c3d4e5");

        try (MockedStatic<ProfileUtil> mockedProfileUtil = mockStatic(ProfileUtil.class)) {
            mockedProfileUtil.when(() -> ProfileUtil.generateUsername(any(), any(), any()))
                            .thenReturn("john.doe");
            mockedProfileUtil.when(ProfileUtil::generatePassword)
                            .thenReturn("a1b2c3d4e5");

            when(trainerDAO.findAll()).thenReturn(Collections.emptyList());
            when(trainerDAO.save(any(Trainer.class))).thenReturn(trainer);

            TrainerDTO savedTrainer = trainerService.save(trainerDTO);

            assertNotNull(savedTrainer);
            assertEquals("john.doe", savedTrainer.getUserName());
            assertEquals("a1b2c3d4e5", savedTrainer.getPassword());
            verify(trainerDAO, times(1)).save(any(Trainer.class));
        }
    }

    @Test
    void testFindTrainerById() {
        Long id = 1L;
        Trainer trainer = createTrainer(id);

        when(trainerDAO.findById(id)).thenReturn(Optional.of(trainer));

        TrainerDTO foundTrainer = trainerService.findById(id);

        assertNotNull(foundTrainer);
        assertEquals("John", foundTrainer.getFirstName());
        assertEquals("Doe", foundTrainer.getLastName());
    }

    @Test
    void testFindAllTrainers() {
        Trainer trainer = createTrainer(1L);

        when(trainerDAO.findAll()).thenReturn(List.of(trainer));

        List<TrainerDTO> trainers = trainerService.findAll();

        assertNotNull(trainers);
        assertEquals(1, trainers.size());
        assertEquals("John", trainers.get(0).getFirstName());
    }

    @Test
    void testDeleteTrainer() {
        Long id = 1L;

        doNothing().when(trainerDAO).delete(eq(id));

        trainerService.delete(id);

        verify(trainerDAO, times(1)).delete(eq(id));
    }

    private TrainerDTO createTrainerDTO() {
        TrainerDTO trainerDTO = new TrainerDTO();
        trainerDTO.setFirstName("John");
        trainerDTO.setLastName("Doe");
        trainerDTO.setSpecialization("Fitness");
        return trainerDTO;
    }

    private Trainer createTrainer(Long id) {
        Trainer trainer = new Trainer();
        trainer.setUserId(id);
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        return trainer;
    }
}