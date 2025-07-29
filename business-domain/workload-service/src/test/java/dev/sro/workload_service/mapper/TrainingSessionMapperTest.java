package dev.sro.workload_service.mapper;

import dev.sro.workload_service.dtos.v1.request.TrainerWorkloadRequest;
import dev.sro.workload_service.entity.TrainingSession;
import dev.sro.workload_service.entity.enums.ActionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TrainingSessionMapperTest {

    @Autowired
    private TrainingSessionMapper mapper;

    @Test
    void shouldMapTrainerWorkloadRequestToTrainingSession() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "trainer.username",
                "John",
                "Doe",
                true,
                LocalDate.of(2024, 3, 15),
                120,
                ActionType.ADD
        );

        TrainingSession result = mapper.toTrainingSession(request);

        assertNotNull(result);
        assertEquals("trainer.username", result.getTrainerUsername());
        assertEquals(LocalDate.of(2024, 3, 15), result.getTrainingDate());
        assertEquals(120, result.getTrainingDuration());
        assertEquals(ActionType.ADD, result.getActionType());
        assertNull(result.getId());
        assertNull(result.getCreatedAt());
        assertNull(result.getUpdatedAt());
    }

    @Test
    void shouldMapTrainerWorkloadRequestWithDeleteAction() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "delete.trainer",
                "Jane",
                "Smith",
                false,
                LocalDate.of(2024, 1, 10),
                60,
                ActionType.DELETE
        );

        TrainingSession result = mapper.toTrainingSession(request);

        assertNotNull(result);
        assertEquals("delete.trainer", result.getTrainerUsername());
        assertEquals(LocalDate.of(2024, 1, 10), result.getTrainingDate());
        assertEquals(60, result.getTrainingDuration());
        assertEquals(ActionType.DELETE, result.getActionType());
        assertNull(result.getId());
        assertNull(result.getCreatedAt());
        assertNull(result.getUpdatedAt());
    }

    @Test
    void shouldMapTrainerWorkloadRequestWithUpdateAction() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "update.trainer",
                "Mike",
                "Johnson",
                true,
                LocalDate.of(2024, 6, 20),
                90,
                ActionType.UPDATE
        );

        TrainingSession result = mapper.toTrainingSession(request);

        assertNotNull(result);
        assertEquals("update.trainer", result.getTrainerUsername());
        assertEquals(LocalDate.of(2024, 6, 20), result.getTrainingDate());
        assertEquals(90, result.getTrainingDuration());
        assertEquals(ActionType.UPDATE, result.getActionType());
        assertNull(result.getId());
        assertNull(result.getCreatedAt());
        assertNull(result.getUpdatedAt());
    }

    @Test
    void shouldMapTrainerWorkloadRequestWithZeroDuration() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "zero.duration.trainer",
                "Zero",
                "Duration",
                true,
                LocalDate.of(2024, 12, 31),
                0,
                ActionType.ADD
        );

        TrainingSession result = mapper.toTrainingSession(request);

        assertNotNull(result);
        assertEquals("zero.duration.trainer", result.getTrainerUsername());
        assertEquals(LocalDate.of(2024, 12, 31), result.getTrainingDate());
        assertEquals(0, result.getTrainingDuration());
        assertEquals(ActionType.ADD, result.getActionType());
        assertNull(result.getId());
        assertNull(result.getCreatedAt());
        assertNull(result.getUpdatedAt());
    }

    @Test
    void shouldMapTrainerWorkloadRequestWithLongDuration() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "long.duration.trainer",
                "Long",
                "Duration",
                true,
                LocalDate.of(2024, 7, 15),
                480,
                ActionType.ADD
        );

        TrainingSession result = mapper.toTrainingSession(request);

        assertNotNull(result);
        assertEquals("long.duration.trainer", result.getTrainerUsername());
        assertEquals(LocalDate.of(2024, 7, 15), result.getTrainingDate());
        assertEquals(480, result.getTrainingDuration());
        assertEquals(ActionType.ADD, result.getActionType());
        assertNull(result.getId());
        assertNull(result.getCreatedAt());
        assertNull(result.getUpdatedAt());
    }

    @Test
    void shouldMapTrainerWorkloadRequestWithSpecialCharactersInUsername() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "trainer.with.dots.and_underscores",
                "Special",
                "Characters",
                true,
                LocalDate.of(2024, 5, 1),
                45,
                ActionType.UPDATE
        );

        TrainingSession result = mapper.toTrainingSession(request);

        assertNotNull(result);
        assertEquals("trainer.with.dots.and_underscores", result.getTrainerUsername());
        assertEquals(LocalDate.of(2024, 5, 1), result.getTrainingDate());
        assertEquals(45, result.getTrainingDuration());
        assertEquals(ActionType.UPDATE, result.getActionType());
        assertNull(result.getId());
        assertNull(result.getCreatedAt());
        assertNull(result.getUpdatedAt());
    }

    @Test
    void shouldMapTrainerWorkloadRequestWithLeapYearDate() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "leap.year.trainer",
                "Leap",
                "Year",
                true,
                LocalDate.of(2024, 2, 29),
                75,
                ActionType.ADD
        );

        TrainingSession result = mapper.toTrainingSession(request);

        assertNotNull(result);
        assertEquals("leap.year.trainer", result.getTrainerUsername());
        assertEquals(LocalDate.of(2024, 2, 29), result.getTrainingDate());
        assertEquals(75, result.getTrainingDuration());
        assertEquals(ActionType.ADD, result.getActionType());
        assertNull(result.getId());
        assertNull(result.getCreatedAt());
        assertNull(result.getUpdatedAt());
    }

    @Test
    void shouldMapTrainerWorkloadRequestWithYearEndDate() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "year.end.trainer",
                "Year",
                "End",
                false,
                LocalDate.of(2024, 12, 31),
                30,
                ActionType.DELETE
        );

        TrainingSession result = mapper.toTrainingSession(request);

        assertNotNull(result);
        assertEquals("year.end.trainer", result.getTrainerUsername());
        assertEquals(LocalDate.of(2024, 12, 31), result.getTrainingDate());
        assertEquals(30, result.getTrainingDuration());
        assertEquals(ActionType.DELETE, result.getActionType());
        assertNull(result.getId());
        assertNull(result.getCreatedAt());
        assertNull(result.getUpdatedAt());
    }

    @Test
    void shouldMapTrainerWorkloadRequestWithYearStartDate() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "year.start.trainer",
                "Year",
                "Start",
                true,
                LocalDate.of(2024, 1, 1),
                60,
                ActionType.ADD
        );

        TrainingSession result = mapper.toTrainingSession(request);

        assertNotNull(result);
        assertEquals("year.start.trainer", result.getTrainerUsername());
        assertEquals(LocalDate.of(2024, 1, 1), result.getTrainingDate());
        assertEquals(60, result.getTrainingDuration());
        assertEquals(ActionType.ADD, result.getActionType());
        assertNull(result.getId());
        assertNull(result.getCreatedAt());
        assertNull(result.getUpdatedAt());
    }

    @Test
    void shouldMapTrainerWorkloadRequestWithShortDuration() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "short.duration.trainer",
                "Short",
                "Duration",
                true,
                LocalDate.of(2024, 8, 10),
                15,
                ActionType.UPDATE
        );

        TrainingSession result = mapper.toTrainingSession(request);

        assertNotNull(result);
        assertEquals("short.duration.trainer", result.getTrainerUsername());
        assertEquals(LocalDate.of(2024, 8, 10), result.getTrainingDate());
        assertEquals(15, result.getTrainingDuration());
        assertEquals(ActionType.UPDATE, result.getActionType());
        assertNull(result.getId());
        assertNull(result.getCreatedAt());
        assertNull(result.getUpdatedAt());
    }

    @Test
    void shouldMapTrainerWorkloadRequestWithInactiveTrainer() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "inactive.trainer",
                "Inactive",
                "Trainer",
                false,
                LocalDate.of(2024, 9, 5),
                120,
                ActionType.DELETE
        );

        TrainingSession result = mapper.toTrainingSession(request);

        assertNotNull(result);
        assertEquals("inactive.trainer", result.getTrainerUsername());
        assertEquals(LocalDate.of(2024, 9, 5), result.getTrainingDate());
        assertEquals(120, result.getTrainingDuration());
        assertEquals(ActionType.DELETE, result.getActionType());
        assertNull(result.getId());
        assertNull(result.getCreatedAt());
        assertNull(result.getUpdatedAt());
    }

    @Test
    void shouldMapTrainerWorkloadRequestWithDifferentYears() {
        TrainerWorkloadRequest request2023 = new TrainerWorkloadRequest(
                "year.2023.trainer",
                "Year",
                "2023",
                true,
                LocalDate.of(2023, 6, 15),
                90,
                ActionType.ADD
        );

        TrainingSession result2023 = mapper.toTrainingSession(request2023);

        assertNotNull(result2023);
        assertEquals("year.2023.trainer", result2023.getTrainerUsername());
        assertEquals(LocalDate.of(2023, 6, 15), result2023.getTrainingDate());
        assertEquals(90, result2023.getTrainingDuration());
        assertEquals(ActionType.ADD, result2023.getActionType());

        TrainerWorkloadRequest request2025 = new TrainerWorkloadRequest(
                "year.2025.trainer",
                "Year",
                "2025",
                true,
                LocalDate.of(2025, 3, 10),
                45,
                ActionType.UPDATE
        );

        TrainingSession result2025 = mapper.toTrainingSession(request2025);

        assertNotNull(result2025);
        assertEquals("year.2025.trainer", result2025.getTrainerUsername());
        assertEquals(LocalDate.of(2025, 3, 10), result2025.getTrainingDate());
        assertEquals(45, result2025.getTrainingDuration());
        assertEquals(ActionType.UPDATE, result2025.getActionType());
    }

    @Test
    void shouldMapTrainerWorkloadRequestWithAllMonths() {
        for (int month = 1; month <= 12; month++) {
            TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                    "month." + month + ".trainer",
                    "Month",
                    String.valueOf(month),
                    true,
                    LocalDate.of(2024, month, 15),
                    60,
                    ActionType.ADD
            );

            TrainingSession result = mapper.toTrainingSession(request);

            assertNotNull(result);
            assertEquals("month." + month + ".trainer", result.getTrainerUsername());
            assertEquals(LocalDate.of(2024, month, 15), result.getTrainingDate());
            assertEquals(60, result.getTrainingDuration());
            assertEquals(ActionType.ADD, result.getActionType());
            assertNull(result.getId());
            assertNull(result.getCreatedAt());
            assertNull(result.getUpdatedAt());
        }
    }

    @Test
    void shouldMapTrainerWorkloadRequestWithDifferentDurations() {
        int[] durations = {15, 30, 45, 60, 90, 120, 180, 240, 300, 480};
        
        for (int duration : durations) {
            TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                    "duration." + duration + ".trainer",
                    "Duration",
                    String.valueOf(duration),
                    true,
                    LocalDate.of(2024, 4, 20),
                    duration,
                    ActionType.ADD
            );

            TrainingSession result = mapper.toTrainingSession(request);

            assertNotNull(result);
            assertEquals("duration." + duration + ".trainer", result.getTrainerUsername());
            assertEquals(LocalDate.of(2024, 4, 20), result.getTrainingDate());
            assertEquals(duration, result.getTrainingDuration());
            assertEquals(ActionType.ADD, result.getActionType());
            assertNull(result.getId());
            assertNull(result.getCreatedAt());
            assertNull(result.getUpdatedAt());
        }
    }
} 