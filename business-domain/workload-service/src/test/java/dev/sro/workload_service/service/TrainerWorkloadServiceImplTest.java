package dev.sro.workload_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

import dev.sro.workload_service.dtos.v1.request.TrainerWorkloadRequest;
import dev.sro.workload_service.dtos.v1.response.TrainerMonthlySummaryResponse;
import dev.sro.workload_service.entity.MonthlySummary;
import dev.sro.workload_service.entity.Trainer;
import dev.sro.workload_service.entity.TrainingSession;
import dev.sro.workload_service.entity.enums.ActionType;
import dev.sro.workload_service.exception.TrainerNotFoundException;
import dev.sro.workload_service.exception.WorkloadProcessingException;
import dev.sro.workload_service.mapper.TrainerMapper;
import dev.sro.workload_service.mapper.TrainerMonthlySummaryMapper;
import dev.sro.workload_service.mapper.TrainingSessionMapper;
import dev.sro.workload_service.repository.MonthlySummaryRepository;
import dev.sro.workload_service.repository.TrainerRepository;
import dev.sro.workload_service.repository.TrainingSessionRepository;
import dev.sro.workload_service.service.impl.TrainerWorkloadServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadServiceImplTest {

    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TrainingSessionRepository trainingSessionRepository;
    @Mock
    private MonthlySummaryRepository monthlySummaryRepository;
    @Mock
    private TrainerMapper trainerMapper;
    @Mock
    private TrainingSessionMapper trainingSessionMapper;
    @Mock
    private TrainerMonthlySummaryMapper trainerMonthlySummaryMapper;

    @InjectMocks
    private TrainerWorkloadServiceImpl trainerWorkloadService;

    private TrainerWorkloadRequest request;
    private Trainer trainer;
    private TrainingSession trainingSession;
    private MonthlySummary monthlySummary;

    @BeforeEach
    void setUp() {
        request = new TrainerWorkloadRequest(
                "test.trainer",
                "Test",
                "Trainer",
                true,
                LocalDate.of(2024, 1, 15),
                60,
                ActionType.ADD);

        trainer = Trainer.builder()
                .username("test.trainer")
                .firstName("Test")
                .lastName("Trainer")
                .isActive(true)
                .build();

        trainingSession = TrainingSession.builder()
                .trainer(trainer)
                .trainingDate(request.trainingDate())
                .trainingDuration(request.trainingDuration())
                .actionType(request.actionType())
                .build();

        monthlySummary = MonthlySummary.builder()
                .trainer(trainer)
                .year(2024)
                .month(1)
                .totalDuration(60)
                .build();
    }

    @Test
    @DisplayName("processTrainerWorkload should process workload for a new trainer")
    void processTrainerWorkload_ForNewTrainer() {
        when(trainerRepository.findByUsername("test.trainer")).thenReturn(Optional.empty());
        when(trainerMapper.toTrainer(request)).thenReturn(trainer);
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);
        when(trainingSessionMapper.toTrainingSession(request)).thenReturn(trainingSession);
        when(monthlySummaryRepository.findByTrainer_UsernameAndYearAndMonth("test.trainer", 2024, 1))
                .thenReturn(Optional.empty());
        when(monthlySummaryRepository.save(any(MonthlySummary.class))).thenReturn(monthlySummary);

        assertDoesNotThrow(() -> trainerWorkloadService.processTrainerWorkload(request));

        verify(trainerRepository).findByUsername("test.trainer");
        verify(trainerMapper).toTrainer(request);
        verify(trainingSessionRepository).save(any(TrainingSession.class));
        verify(monthlySummaryRepository, times(2)).save(any(MonthlySummary.class));
    }

    @Test
    @DisplayName("processTrainerWorkload should process workload for an existing trainer")
    void processTrainerWorkload_ForExistingTrainer() {
        when(trainerRepository.findByUsername("test.trainer")).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);
        when(trainingSessionMapper.toTrainingSession(request)).thenReturn(trainingSession);
        when(monthlySummaryRepository.findByTrainer_UsernameAndYearAndMonth("test.trainer", 2024, 1))
                .thenReturn(Optional.of(monthlySummary));

        assertDoesNotThrow(() -> trainerWorkloadService.processTrainerWorkload(request));

        verify(trainerRepository).findByUsername("test.trainer");
        verify(trainerMapper, never()).toTrainer(any());
        verify(trainingSessionRepository).save(any(TrainingSession.class));
        verify(monthlySummaryRepository).save(any(MonthlySummary.class));
    }

    @Test
    @DisplayName("processTrainerWorkload should subtract duration for DELETE action")
    void processTrainerWorkload_DeleteAction() {
        request = new TrainerWorkloadRequest(
                "test.trainer", "Test", "Trainer", true,
                LocalDate.of(2024, 1, 15), 30, ActionType.DELETE);
        trainingSession.setActionType(ActionType.DELETE);

        when(trainerRepository.findByUsername("test.trainer")).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);
        when(trainingSessionMapper.toTrainingSession(request)).thenReturn(trainingSession);
        when(monthlySummaryRepository.findByTrainer_UsernameAndYearAndMonth("test.trainer", 2024, 1))
                .thenReturn(Optional.of(monthlySummary));

        trainerWorkloadService.processTrainerWorkload(request);

        assertEquals(30, monthlySummary.getTotalDuration());
        verify(monthlySummaryRepository).save(monthlySummary);
    }

    @Test
    @DisplayName("processTrainerWorkload should update existing trainer's profile")
    void processTrainerWorkload_UpdateTrainerProfile() {
        TrainerWorkloadRequest updateRequest = new TrainerWorkloadRequest(
                "test.trainer", "UpdatedFirstName", "UpdatedLastName", false,
                LocalDate.of(2024, 1, 15), 30, ActionType.ADD);
        when(trainerRepository.findByUsername("test.trainer")).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);
        when(trainingSessionMapper.toTrainingSession(updateRequest)).thenReturn(new TrainingSession());
        when(monthlySummaryRepository.findByTrainer_UsernameAndYearAndMonth(anyString(), anyInt(), anyInt()))
                .thenReturn(Optional.of(monthlySummary));

        trainerWorkloadService.processTrainerWorkload(updateRequest);

        verify(trainerRepository).save(trainer);
        assertEquals("UpdatedFirstName", trainer.getFirstName());
        assertEquals("UpdatedLastName", trainer.getLastName());
        assertFalse(trainer.getIsActive());
    }

    @Test
    @DisplayName("processTrainerWorkload should create new monthly summary if not exists")
    void processTrainerWorkload_CreatesNewSummary() {
        when(trainerRepository.findByUsername("test.trainer")).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);
        when(trainingSessionMapper.toTrainingSession(request)).thenReturn(trainingSession);
        when(monthlySummaryRepository.findByTrainer_UsernameAndYearAndMonth("test.trainer", 2024, 1))
                .thenReturn(Optional.empty());

        when(monthlySummaryRepository.save(any(MonthlySummary.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        trainerWorkloadService.processTrainerWorkload(request);

        verify(monthlySummaryRepository, times(2)).save(any(MonthlySummary.class));
    }

    @Test
    @DisplayName("processTrainerWorkload should throw WorkloadProcessingException when getOrCreateTrainer fails")
    void processTrainerWorkload_FailsOnGetOrCreateTrainer() {
        when(trainerRepository.findByUsername(request.trainerUsername())).thenReturn(Optional.empty());
        when(trainerMapper.toTrainer(request)).thenReturn(trainer);
        when(trainerRepository.save(any(Trainer.class))).thenThrow(new RuntimeException("DB error on save trainer"));

        assertThrows(WorkloadProcessingException.class, () -> trainerWorkloadService.processTrainerWorkload(request));
    }

    @Test
    @DisplayName("processTrainerWorkload should throw WorkloadProcessingException when updateMonthlySummary fails")
    void processTrainerWorkload_FailsOnUpdateMonthlySummary() {
        when(trainerRepository.findByUsername(request.trainerUsername())).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);
        when(trainingSessionMapper.toTrainingSession(request)).thenReturn(trainingSession);
        when(monthlySummaryRepository.findByTrainer_UsernameAndYearAndMonth(anyString(), anyInt(), anyInt()))
                .thenReturn(Optional.of(monthlySummary));
        doThrow(new RuntimeException("DB error on save summary")).when(monthlySummaryRepository).save(monthlySummary);

        assertThrows(WorkloadProcessingException.class, () -> trainerWorkloadService.processTrainerWorkload(request));
    }

    @Test
    @DisplayName("getTrainerMonthlySummary should return summary for existing trainer")
    void getTrainerMonthlySummary_ExistingTrainer() {
        when(trainerRepository.findByUsername("test.trainer")).thenReturn(Optional.of(trainer));
        when(monthlySummaryRepository.findByTrainer_Username("test.trainer")).thenReturn(List.of(monthlySummary));
        when(trainerMonthlySummaryMapper.toResponse(trainer, List.of(monthlySummary)))
                .thenReturn(mock(TrainerMonthlySummaryResponse.class));

        TrainerMonthlySummaryResponse response = trainerWorkloadService.getTrainerMonthlySummary("test.trainer");

        assertNotNull(response);
        verify(trainerRepository).findByUsername("test.trainer");
        verify(monthlySummaryRepository).findByTrainer_Username("test.trainer");
    }

    @Test
    @DisplayName("getTrainerMonthlySummary should return empty summary for trainer with no sessions")
    void getTrainerMonthlySummary_TrainerWithNoSessions() {
        when(trainerRepository.findByUsername("test.trainer")).thenReturn(Optional.of(trainer));
        when(monthlySummaryRepository.findByTrainer_Username("test.trainer")).thenReturn(Collections.emptyList());
        when(trainerMonthlySummaryMapper.toResponse(trainer, Collections.emptyList()))
                .thenReturn(new TrainerMonthlySummaryResponse("test.trainer", "Test", "Trainer", true,
                        Collections.emptyList()));

        TrainerMonthlySummaryResponse response = trainerWorkloadService.getTrainerMonthlySummary("test.trainer");

        assertNotNull(response);
        assertTrue(response.years().isEmpty());
    }

    @Test
    @DisplayName("getTrainerMonthlySummary should throw TrainerNotFoundException for non-existing trainer")
    void getTrainerMonthlySummary_NonExistingTrainer() {
        when(trainerRepository.findByUsername("unknown.trainer")).thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class,
                () -> trainerWorkloadService.getTrainerMonthlySummary("unknown.trainer"));
    }

    @Test
    @DisplayName("getTrainerMonthlySummary should throw IllegalArgumentException for null username")
    void getTrainerMonthlySummary_NullUsername() {
        assertThrows(IllegalArgumentException.class, () -> trainerWorkloadService.getTrainerMonthlySummary(null));
    }

    @Test
    @DisplayName("getTrainerMonthlySummary by year should return summary")
    void getTrainerMonthlySummary_ByYear() {
        when(trainerRepository.findByUsername("test.trainer")).thenReturn(Optional.of(trainer));
        when(monthlySummaryRepository.findByTrainer_UsernameAndYear("test.trainer", 2024))
                .thenReturn(List.of(monthlySummary));
        when(trainerMonthlySummaryMapper.toResponse(trainer, List.of(monthlySummary)))
                .thenReturn(mock(TrainerMonthlySummaryResponse.class));

        TrainerMonthlySummaryResponse response = trainerWorkloadService.getTrainerMonthlySummary("test.trainer", 2024);

        assertNotNull(response);
        verify(monthlySummaryRepository).findByTrainer_UsernameAndYear("test.trainer", 2024);
    }

    @Test
    @DisplayName("getTrainerMonthlySummary by year should throw IllegalArgumentException for invalid year")
    void getTrainerMonthlySummary_InvalidYear() {
        assertThrows(IllegalArgumentException.class,
                () -> trainerWorkloadService.getTrainerMonthlySummary("test.trainer", 999));
    }

    @Test
    @DisplayName("getTrainerMonthlySummary by year and month should return summary")
    void getTrainerMonthlySummary_ByYearAndMonth() {
        when(trainerRepository.findByUsername("test.trainer")).thenReturn(Optional.of(trainer));
        when(monthlySummaryRepository.findByTrainer_UsernameAndYearAndMonth("test.trainer", 2024, 1))
                .thenReturn(Optional.of(monthlySummary));
        when(trainerMonthlySummaryMapper.toResponse(trainer, List.of(monthlySummary)))
                .thenReturn(mock(TrainerMonthlySummaryResponse.class));

        TrainerMonthlySummaryResponse response = trainerWorkloadService.getTrainerMonthlySummary("test.trainer", 2024,
                1);

        assertNotNull(response);
        verify(monthlySummaryRepository).findByTrainer_UsernameAndYearAndMonth("test.trainer", 2024, 1);
    }

    @Test
    @DisplayName("getTrainerMonthlySummary by month should throw IllegalArgumentException for invalid month")
    void getTrainerMonthlySummary_InvalidMonth() {
        assertThrows(IllegalArgumentException.class,
                () -> trainerWorkloadService.getTrainerMonthlySummary("test.trainer", 2024, 13));
    }

    @Test
    @DisplayName("processTrainerWorkload should throw WorkloadProcessingException on repository failure")
    void processTrainerWorkload_RepositoryFailure() {
        when(trainerRepository.findByUsername(request.trainerUsername()))
                .thenThrow(new RuntimeException("Database error"));

        assertThrows(WorkloadProcessingException.class, () -> trainerWorkloadService.processTrainerWorkload(request));
    }

    @Test
    @DisplayName("getTrainerMonthlySummary should throw WorkloadProcessingException on repository failure")
    void getTrainerMonthlySummary_RepositoryFailure() {
        when(trainerRepository.findByUsername("test.trainer")).thenThrow(new RuntimeException("Database error"));

        assertThrows(WorkloadProcessingException.class,
                () -> trainerWorkloadService.getTrainerMonthlySummary("test.trainer"));
    }

    @Test
    @DisplayName("getTrainerMonthlySummary by year should throw WorkloadProcessingException on failure")
    void getTrainerMonthlySummary_ByYear_RepositoryFailure() {
        when(trainerRepository.findByUsername("test.trainer")).thenThrow(new RuntimeException("Database error"));

        assertThrows(WorkloadProcessingException.class,
                () -> trainerWorkloadService.getTrainerMonthlySummary("test.trainer", 2024));
    }

    @Test
    @DisplayName("getTrainerMonthlySummary by month/year should throw WorkloadProcessingException on failure")
    void getTrainerMonthlySummary_ByYearAndMonth_RepositoryFailure() {
        when(trainerRepository.findByUsername("test.trainer")).thenThrow(new RuntimeException("Database error"));

        assertThrows(WorkloadProcessingException.class,
                () -> trainerWorkloadService.getTrainerMonthlySummary("test.trainer", 2024, 1));
    }

    @Test
    @DisplayName("getTrainerMonthlySummary by year should throw TrainerNotFoundException")
    void getTrainerMonthlySummary_ByYear_TrainerNotFound() {
        when(trainerRepository.findByUsername("unknown.trainer")).thenReturn(Optional.empty());
        assertThrows(TrainerNotFoundException.class,
                () -> trainerWorkloadService.getTrainerMonthlySummary("unknown.trainer", 2024));
    }

    @Test
    @DisplayName("getTrainerMonthlySummary by year/month should throw TrainerNotFoundException")
    void getTrainerMonthlySummary_ByYearAndMonth_TrainerNotFound() {
        when(trainerRepository.findByUsername("unknown.trainer")).thenReturn(Optional.empty());
        assertThrows(TrainerNotFoundException.class,
                () -> trainerWorkloadService.getTrainerMonthlySummary("unknown.trainer", 2024, 1));
    }

    @Test
    @DisplayName("getTrainerMonthlySummary by year should return empty summary for no data")
    void getTrainerMonthlySummary_ByYear_NoData() {
        when(trainerRepository.findByUsername("test.trainer")).thenReturn(Optional.of(trainer));
        when(monthlySummaryRepository.findByTrainer_UsernameAndYear("test.trainer", 2023))
                .thenReturn(Collections.emptyList());
        when(trainerMonthlySummaryMapper.toResponse(trainer, Collections.emptyList()))
                .thenReturn(new TrainerMonthlySummaryResponse("test.trainer", "Test", "Trainer", true,
                        Collections.emptyList()));

        TrainerMonthlySummaryResponse response = trainerWorkloadService.getTrainerMonthlySummary("test.trainer", 2023);

        assertNotNull(response);
        assertTrue(response.years().isEmpty());
    }

    @Test
    @DisplayName("getTrainerMonthlySummary by year/month should return empty summary for no data")
    void getTrainerMonthlySummary_ByYearAndMonth_NoData() {
        when(trainerRepository.findByUsername("test.trainer")).thenReturn(Optional.of(trainer));
        when(monthlySummaryRepository.findByTrainer_UsernameAndYearAndMonth("test.trainer", 2023, 5))
                .thenReturn(Optional.empty());
        when(trainerMonthlySummaryMapper.toResponse(trainer, Collections.emptyList()))
                .thenReturn(new TrainerMonthlySummaryResponse("test.trainer", "Test", "Trainer", true,
                        Collections.emptyList()));

        TrainerMonthlySummaryResponse response = trainerWorkloadService.getTrainerMonthlySummary("test.trainer", 2023,
                5);

        assertNotNull(response);
        assertTrue(response.years().isEmpty());
    }
}