package dev.sro.workload_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

import dev.sro.workload_service.dtos.v1.request.TrainerWorkloadRequest;
import dev.sro.workload_service.dtos.v1.response.TrainerMonthlySummaryResponse;
import dev.sro.workload_service.entity.TrainerTrainingSummary;
import dev.sro.workload_service.entity.TrainingSession;
import dev.sro.workload_service.entity.YearSummary;
import dev.sro.workload_service.entity.MonthSummary;
import dev.sro.workload_service.entity.enums.ActionType;
import dev.sro.workload_service.exception.InvalidWorkloadDataException;
import dev.sro.workload_service.exception.TrainerNotFoundException;
import dev.sro.workload_service.exception.WorkloadProcessingException;
import dev.sro.workload_service.mapper.TrainerTrainingSummaryMapper;
import dev.sro.workload_service.mapper.TrainingSessionMapper;
import dev.sro.workload_service.repository.TrainerTrainingSummaryRepository;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadServiceImplTest {

        @Mock
        private TrainerTrainingSummaryRepository trainerTrainingSummaryRepository;
        @Mock
        private TrainingSessionRepository trainingSessionRepository;
        @Mock
        private TrainerTrainingSummaryMapper trainerTrainingSummaryMapper;
        @Mock
        private TrainingSessionMapper trainingSessionMapper;

        @InjectMocks
        private TrainerWorkloadServiceImpl trainerWorkloadService;

        private TrainerWorkloadRequest request;
        private TrainerTrainingSummary trainerSummary;
        private TrainingSession trainingSession;
        private YearSummary yearSummary;
        private MonthSummary monthSummary;

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

                monthSummary = MonthSummary.builder()
                                .month(1)
                                .trainingsSummaryDuration(60)
                                .build();

                yearSummary = YearSummary.builder()
                                .year(2024)
                                .months(List.of(monthSummary))
                                .build();

                trainerSummary = TrainerTrainingSummary.builder()
                                .id("trainer-123")
                                .trainerUsername("test.trainer")
                                .trainerFirstName("Test")
                                .trainerLastName("Trainer")
                                .trainerStatus(true)
                                .years(List.of(yearSummary))
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                trainingSession = TrainingSession.builder()
                                .id("session-123")
                                .trainerUsername("test.trainer")
                                .trainingDate(request.trainingDate())
                                .trainingDuration(request.trainingDuration())
                                .actionType(request.actionType())
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
        }

        @Test
        @DisplayName("processTrainerWorkload should process workload for a new trainer")
        void processTrainerWorkload_ForNewTrainer() {
                when(trainerTrainingSummaryRepository.findByTrainerUsername("test.trainer"))
                                .thenReturn(Optional.empty());
                when(trainerTrainingSummaryMapper.toTrainerTrainingSummary(request)).thenReturn(trainerSummary);
                when(trainerTrainingSummaryRepository.save(any(TrainerTrainingSummary.class)))
                                .thenReturn(trainerSummary);
                when(trainingSessionMapper.toTrainingSession(request)).thenReturn(trainingSession);
                when(trainingSessionRepository.save(any(TrainingSession.class))).thenReturn(trainingSession);

                assertDoesNotThrow(() -> trainerWorkloadService.processTrainerWorkload(request));

                verify(trainerTrainingSummaryRepository).findByTrainerUsername("test.trainer");
                verify(trainerTrainingSummaryMapper).toTrainerTrainingSummary(request);
                verify(trainerTrainingSummaryRepository, times(2)).save(any(TrainerTrainingSummary.class));
                verify(trainingSessionRepository).save(any(TrainingSession.class));
        }

        @Test
        @DisplayName("processTrainerWorkload should process workload for an existing trainer")
        void processTrainerWorkload_ForExistingTrainer() {
                when(trainerTrainingSummaryRepository.findByTrainerUsername("test.trainer"))
                                .thenReturn(Optional.of(trainerSummary));
                when(trainerTrainingSummaryRepository.save(any(TrainerTrainingSummary.class)))
                                .thenReturn(trainerSummary);
                when(trainingSessionMapper.toTrainingSession(request)).thenReturn(trainingSession);
                when(trainingSessionRepository.save(any(TrainingSession.class))).thenReturn(trainingSession);

                assertDoesNotThrow(() -> trainerWorkloadService.processTrainerWorkload(request));

                verify(trainerTrainingSummaryRepository).findByTrainerUsername("test.trainer");
                verify(trainerTrainingSummaryMapper, never()).toTrainerTrainingSummary(any());
                verify(trainerTrainingSummaryRepository, times(2)).save(any(TrainerTrainingSummary.class));
                verify(trainingSessionRepository).save(any(TrainingSession.class));
        }

        @Test
        @DisplayName("processTrainerWorkload should subtract duration for DELETE action")
        void processTrainerWorkload_DeleteAction() {
                request = new TrainerWorkloadRequest(
                                "test.trainer", "Test", "Trainer", true,
                                LocalDate.of(2024, 1, 15), 30, ActionType.DELETE);

                TrainingSession deleteSession = TrainingSession.builder()
                                .id("session-123")
                                .trainerUsername("test.trainer")
                                .trainingDate(request.trainingDate())
                                .trainingDuration(request.trainingDuration())
                                .actionType(ActionType.DELETE)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                when(trainerTrainingSummaryRepository.findByTrainerUsername("test.trainer"))
                                .thenReturn(Optional.of(trainerSummary));
                when(trainerTrainingSummaryRepository.save(any(TrainerTrainingSummary.class)))
                                .thenReturn(trainerSummary);
                when(trainingSessionMapper.toTrainingSession(request)).thenReturn(deleteSession);
                when(trainingSessionRepository.save(any(TrainingSession.class))).thenReturn(deleteSession);

                trainerWorkloadService.processTrainerWorkload(request);

                verify(trainerTrainingSummaryRepository, times(2)).save(any(TrainerTrainingSummary.class));
        }

        @Test
        @DisplayName("processTrainerWorkload should handle UPDATE action")
        void processTrainerWorkload_UpdateAction() {
                request = new TrainerWorkloadRequest(
                                "test.trainer", "Test", "Trainer", true,
                                LocalDate.of(2024, 1, 15), 90, ActionType.UPDATE);

                TrainingSession updateSession = TrainingSession.builder()
                                .id("session-123")
                                .trainerUsername("test.trainer")
                                .trainingDate(request.trainingDate())
                                .trainingDuration(90)
                                .actionType(ActionType.UPDATE)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                when(trainerTrainingSummaryRepository.findByTrainerUsername("test.trainer"))
                                .thenReturn(Optional.of(trainerSummary));
                when(trainerTrainingSummaryRepository.save(any(TrainerTrainingSummary.class)))
                                .thenReturn(trainerSummary);
                when(trainingSessionMapper.toTrainingSession(request)).thenReturn(updateSession);
                when(trainingSessionRepository.findByTrainerUsernameAndTrainingDate("test.trainer",
                                request.trainingDate()))
                                .thenReturn(Optional.of(updateSession));
                when(trainingSessionRepository.save(any(TrainingSession.class))).thenReturn(updateSession);

                trainerWorkloadService.processTrainerWorkload(request);

                verify(trainerTrainingSummaryRepository, times(2)).save(any(TrainerTrainingSummary.class));
                verify(trainingSessionRepository, times(1)).save(any(TrainingSession.class));
        }

        @Test
        @DisplayName("processTrainerWorkload should update existing trainer's profile")
        void processTrainerWorkload_UpdateTrainerProfile() {
                TrainerWorkloadRequest updateRequest = new TrainerWorkloadRequest(
                                "test.trainer", "UpdatedFirstName", "UpdatedLastName", false,
                                LocalDate.of(2024, 1, 15), 30, ActionType.ADD);
                when(trainerTrainingSummaryRepository.findByTrainerUsername("test.trainer"))
                                .thenReturn(Optional.of(trainerSummary));
                when(trainerTrainingSummaryRepository.save(any(TrainerTrainingSummary.class)))
                                .thenReturn(trainerSummary);
                when(trainingSessionMapper.toTrainingSession(updateRequest)).thenReturn(new TrainingSession());
                when(trainingSessionRepository.save(any(TrainingSession.class))).thenReturn(trainingSession);

                trainerWorkloadService.processTrainerWorkload(updateRequest);

                verify(trainerTrainingSummaryRepository, times(2)).save(any(TrainerTrainingSummary.class));
        }

        @Test
        @DisplayName("processTrainerWorkload should throw WorkloadProcessingException when getOrCreateTrainer fails")
        void processTrainerWorkload_FailsOnGetOrCreateTrainer() {
                when(trainerTrainingSummaryRepository.findByTrainerUsername(request.trainerUsername()))
                                .thenReturn(Optional.empty());
                when(trainerTrainingSummaryMapper.toTrainerTrainingSummary(request)).thenReturn(trainerSummary);
                when(trainerTrainingSummaryRepository.save(any(TrainerTrainingSummary.class)))
                                .thenThrow(new RuntimeException("DB error on save trainer"));

                assertThrows(WorkloadProcessingException.class,
                                () -> trainerWorkloadService.processTrainerWorkload(request));
        }

        @Test
        @DisplayName("getTrainerMonthlySummary should return summary for existing trainer")
        void getTrainerMonthlySummary_ExistingTrainer() {
                TrainerMonthlySummaryResponse expectedResponse = TrainerMonthlySummaryResponse.builder()
                                .trainerUsername("test.trainer")
                                .trainerFirstName("Test")
                                .trainerLastName("Trainer")
                                .trainerStatus(true)
                                .years(Collections.emptyList())
                                .build();

                when(trainerTrainingSummaryRepository.findByTrainerUsername("test.trainer"))
                                .thenReturn(Optional.of(trainerSummary));
                when(trainerTrainingSummaryMapper.toResponse(trainerSummary)).thenReturn(expectedResponse);

                TrainerMonthlySummaryResponse response = trainerWorkloadService
                                .getTrainerMonthlySummary("test.trainer");

                assertNotNull(response);
                assertEquals("test.trainer", response.trainerUsername());
                verify(trainerTrainingSummaryRepository).findByTrainerUsername("test.trainer");
                verify(trainerTrainingSummaryMapper).toResponse(trainerSummary);
        }

        @Test
        @DisplayName("getTrainerMonthlySummary should throw TrainerNotFoundException for non-existing trainer")
        void getTrainerMonthlySummary_NonExistingTrainer() {
                when(trainerTrainingSummaryRepository.findByTrainerUsername("unknown.trainer"))
                                .thenReturn(Optional.empty());

                assertThrows(TrainerNotFoundException.class,
                                () -> trainerWorkloadService.getTrainerMonthlySummary("unknown.trainer"));
        }

        @Test
        @DisplayName("getTrainerMonthlySummary should throw IllegalArgumentException for null username")
        void getTrainerMonthlySummary_NullUsername() {
                assertThrows(IllegalArgumentException.class,
                                () -> trainerWorkloadService.getTrainerMonthlySummary(null));
        }

        @Test
        @DisplayName("getTrainerMonthlySummary by year should return summary")
        void getTrainerMonthlySummary_ByYear() {
                TrainerMonthlySummaryResponse expectedResponse = TrainerMonthlySummaryResponse.builder()
                                .trainerUsername("test.trainer")
                                .trainerFirstName("Test")
                                .trainerLastName("Trainer")
                                .trainerStatus(true)
                                .years(Collections.emptyList())
                                .build();

                when(trainerTrainingSummaryRepository.findByTrainerUsername("test.trainer"))
                                .thenReturn(Optional.of(trainerSummary));
                when(trainerTrainingSummaryMapper.toResponseForYear(trainerSummary, 2024)).thenReturn(expectedResponse);

                TrainerMonthlySummaryResponse response = trainerWorkloadService.getTrainerMonthlySummary("test.trainer",
                                2024);

                assertNotNull(response);
                verify(trainerTrainingSummaryRepository).findByTrainerUsername("test.trainer");
                verify(trainerTrainingSummaryMapper).toResponseForYear(trainerSummary, 2024);
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
                TrainerMonthlySummaryResponse expectedResponse = TrainerMonthlySummaryResponse.builder()
                                .trainerUsername("test.trainer")
                                .trainerFirstName("Test")
                                .trainerLastName("Trainer")
                                .trainerStatus(true)
                                .years(Collections.emptyList())
                                .build();

                when(trainerTrainingSummaryRepository.findByTrainerUsername("test.trainer"))
                                .thenReturn(Optional.of(trainerSummary));
                when(trainerTrainingSummaryMapper.toResponseForYearAndMonth(trainerSummary, 2024, 1))
                                .thenReturn(expectedResponse);

                TrainerMonthlySummaryResponse response = trainerWorkloadService.getTrainerMonthlySummary("test.trainer",
                                2024, 1);

                assertNotNull(response);
                verify(trainerTrainingSummaryRepository).findByTrainerUsername("test.trainer");
                verify(trainerTrainingSummaryMapper).toResponseForYearAndMonth(trainerSummary, 2024, 1);
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
                when(trainerTrainingSummaryRepository.findByTrainerUsername(request.trainerUsername()))
                                .thenThrow(new RuntimeException("Database error"));

                assertThrows(WorkloadProcessingException.class,
                                () -> trainerWorkloadService.processTrainerWorkload(request));
        }

        @Test
        @DisplayName("getTrainerMonthlySummary should throw WorkloadProcessingException on repository failure")
        void getTrainerMonthlySummary_RepositoryFailure() {
                when(trainerTrainingSummaryRepository.findByTrainerUsername("test.trainer"))
                                .thenThrow(new RuntimeException("Database error"));

                assertThrows(WorkloadProcessingException.class,
                                () -> trainerWorkloadService.getTrainerMonthlySummary("test.trainer"));
        }

        @Test
        @DisplayName("getTrainerMonthlySummary by year should throw WorkloadProcessingException on failure")
        void getTrainerMonthlySummary_ByYear_RepositoryFailure() {
                when(trainerTrainingSummaryRepository.findByTrainerUsername("test.trainer"))
                                .thenThrow(new RuntimeException("Database error"));

                assertThrows(WorkloadProcessingException.class,
                                () -> trainerWorkloadService.getTrainerMonthlySummary("test.trainer", 2024));
        }

        @Test
        @DisplayName("getTrainerMonthlySummary by month/year should throw WorkloadProcessingException on failure")
        void getTrainerMonthlySummary_ByYearAndMonth_RepositoryFailure() {
                when(trainerTrainingSummaryRepository.findByTrainerUsername("test.trainer"))
                                .thenThrow(new RuntimeException("Database error"));

                assertThrows(WorkloadProcessingException.class,
                                () -> trainerWorkloadService.getTrainerMonthlySummary("test.trainer", 2024, 1));
        }

        @Test
        @DisplayName("getTrainerMonthlySummary by year should throw TrainerNotFoundException")
        void getTrainerMonthlySummary_ByYear_TrainerNotFound() {
                when(trainerTrainingSummaryRepository.findByTrainerUsername("unknown.trainer"))
                                .thenReturn(Optional.empty());
                assertThrows(TrainerNotFoundException.class,
                                () -> trainerWorkloadService.getTrainerMonthlySummary("unknown.trainer", 2024));
        }

        @Test
        @DisplayName("getTrainerMonthlySummary by year/month should throw TrainerNotFoundException")
        void getTrainerMonthlySummary_ByYearAndMonth_TrainerNotFound() {
                when(trainerTrainingSummaryRepository.findByTrainerUsername("unknown.trainer"))
                                .thenReturn(Optional.empty());
                assertThrows(TrainerNotFoundException.class,
                                () -> trainerWorkloadService.getTrainerMonthlySummary("unknown.trainer", 2024, 1));
        }

        @Test
        @DisplayName("processTrainerWorkload should validate request data")
        void processTrainerWorkload_ValidatesRequest() {
                TrainerWorkloadRequest invalidRequest = new TrainerWorkloadRequest(
                                "", null, null, true, null, -1, null);

                assertThrows(InvalidWorkloadDataException.class,
                                () -> trainerWorkloadService.processTrainerWorkload(invalidRequest));
        }

        @Test
        @DisplayName("processTrainerWorkload should validate trainer username")
        void processTrainerWorkload_ValidatesTrainerUsername() {
                TrainerWorkloadRequest invalidRequest = new TrainerWorkloadRequest(
                                "", "Test", "Trainer", true, LocalDate.of(2024, 1, 15), 60, ActionType.ADD);

                InvalidWorkloadDataException exception = assertThrows(InvalidWorkloadDataException.class,
                                () -> trainerWorkloadService.processTrainerWorkload(invalidRequest));

                assertTrue(exception.getMessage().contains("trainerUsername"));
                assertTrue(exception.getMessage().contains("Trainer username is required"));
        }

        @Test
        @DisplayName("processTrainerWorkload should validate training duration")
        void processTrainerWorkload_ValidatesTrainingDuration() {
                TrainerWorkloadRequest invalidRequest = new TrainerWorkloadRequest(
                                "test.trainer", "Test", "Trainer", true, LocalDate.of(2024, 1, 15), -1, ActionType.ADD);

                InvalidWorkloadDataException exception = assertThrows(InvalidWorkloadDataException.class,
                                () -> trainerWorkloadService.processTrainerWorkload(invalidRequest));

                assertTrue(exception.getMessage().contains("trainingDuration"));
                assertTrue(exception.getMessage().contains("Training duration must be positive"));
        }

        @Test
        @DisplayName("processTrainerWorkload should validate training date")
        void processTrainerWorkload_ValidatesTrainingDate() {
                TrainerWorkloadRequest invalidRequest = new TrainerWorkloadRequest(
                                "test.trainer", "Test", "Trainer", true, null, 60, ActionType.ADD);

                InvalidWorkloadDataException exception = assertThrows(InvalidWorkloadDataException.class,
                                () -> trainerWorkloadService.processTrainerWorkload(invalidRequest));

                assertTrue(exception.getMessage().contains("trainingDate"));
                assertTrue(exception.getMessage().contains("Training date is required"));
        }

        @Test
        @DisplayName("processTrainerWorkload should validate action type")
        void processTrainerWorkload_ValidatesActionType() {
                TrainerWorkloadRequest invalidRequest = new TrainerWorkloadRequest(
                                "test.trainer", "Test", "Trainer", true, LocalDate.of(2024, 1, 15), 60, null);

                InvalidWorkloadDataException exception = assertThrows(InvalidWorkloadDataException.class,
                                () -> trainerWorkloadService.processTrainerWorkload(invalidRequest));

                assertTrue(exception.getMessage().contains("actionType"));
                assertTrue(exception.getMessage().contains("Action type is required"));
        }
}