package com.sro.SpringCoreTask1.service;

import com.sro.SpringCoreTask1.dao.TraineeDAO;
import com.sro.SpringCoreTask1.dto.TraineeDTO;
import com.sro.SpringCoreTask1.mappers.TraineeMapper;
import com.sro.SpringCoreTask1.models.Trainee;
import com.sro.SpringCoreTask1.util.ProfileUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TraineeServiceTest {

    @Mock
    private TraineeDAO traineeDAO;

    @InjectMocks
    private TraineeService traineeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveTrainee() {
        TraineeDTO traineeDTO = createTraineeDTO();
        Trainee trainee = TraineeMapper.toEntity(traineeDTO);
        trainee.setUserName("john.doe");
        trainee.setPassword("a1b2c3d4e5");

        try (MockedStatic<ProfileUtil> mockedProfileUtil = mockStatic(ProfileUtil.class)) {
            mockedProfileUtil.when(() -> ProfileUtil.generateUsername(any(), any(), any()))
                            .thenReturn("Bryan.Lopez");
            mockedProfileUtil.when(ProfileUtil::generatePassword)
                            .thenReturn("a1b2c3d4e5");

            when(traineeDAO.findAll()).thenReturn(Collections.emptyList());
            when(traineeDAO.save(any(Trainee.class))).thenReturn(trainee);

            TraineeDTO savedTrainee = traineeService.save(traineeDTO);

            assertNotNull(savedTrainee);
            assertEquals("john.doe", savedTrainee.getUserName());
            assertEquals("a1b2c3d4e5", savedTrainee.getPassword());
            verify(traineeDAO, times(1)).save(any(Trainee.class));
        }
    }

    @Test
    void testFindTraineeById() {
        Long id = 1L;
        Trainee trainee = createTrainee(id);

        when(traineeDAO.findById(id)).thenReturn(Optional.of(trainee));

        TraineeDTO foundTrainee = traineeService.findById(id);

        assertNotNull(foundTrainee);
        assertEquals("John", foundTrainee.getFirstName());
        assertEquals("Doe", foundTrainee.getLastName());
    }

    @Test
    void testFindAllTrainees() {
        Trainee trainee = createTrainee(1L);

        when(traineeDAO.findAll()).thenReturn(List.of(trainee));

        List<TraineeDTO> trainees = traineeService.findAll();

        assertNotNull(trainees);
        assertEquals(1, trainees.size());
        assertEquals("John", trainees.get(0).getFirstName());
    }

    @Test
    void testDeleteTrainee() {
        Long id = 1L;

        doNothing().when(traineeDAO).delete(eq(id));

        traineeService.delete(id);

        verify(traineeDAO, times(1)).delete(eq(id));
    }

    private TraineeDTO createTraineeDTO() {
        TraineeDTO traineeDTO = new TraineeDTO();
        traineeDTO.setFirstName("Bryan");
        traineeDTO.setLastName("Lopez");
        traineeDTO.setAddress("123 Main St");
        traineeDTO.setDateOfBirth(LocalDate.of(1990, 5, 15));
        return traineeDTO;
    }

    private Trainee createTrainee(Long id) {
        Trainee trainee = new Trainee();
        trainee.setUserId(id);
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        return trainee;
    }
}