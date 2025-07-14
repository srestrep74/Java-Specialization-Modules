package dev.sro.workload_service.mapper;

import dev.sro.workload_service.dtos.v1.request.TrainerWorkloadRequest;
import dev.sro.workload_service.entity.TrainingSession;
import dev.sro.workload_service.entity.enums.ActionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TrainingSessionMapperTest {

    private final TrainingSessionMapper mapper = TrainingSessionMapper.INSTANCE;

    @Test
    void shouldMapTrainerWorkloadRequestToTrainingSession() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "trainer.username",
                "John",
                "Doe",
                true,
                LocalDate.of(2024, 1, 15),
                60,
                ActionType.ADD);

        TrainingSession result = mapper.toTrainingSession(request);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getTrainer());
        assertNull(result.getCreatedAt());
        assertNull(result.getUpdatedAt());
        assertEquals(LocalDate.of(2024, 1, 15), result.getTrainingDate());
        assertEquals(60, result.getTrainingDuration());
        assertEquals(ActionType.ADD, result.getActionType());
    }

    @Test
    void shouldMapTrainerWorkloadRequestWithDeleteAction() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "trainer.username",
                "Jane",
                "Smith",
                false,
                LocalDate.of(2024, 2, 20),
                90,
                ActionType.DELETE);

        TrainingSession result = mapper.toTrainingSession(request);

        assertNotNull(result);
        assertEquals(LocalDate.of(2024, 2, 20), result.getTrainingDate());
        assertEquals(90, result.getTrainingDuration());
        assertEquals(ActionType.DELETE, result.getActionType());
    }

    @Test
    void shouldMapTrainerWorkloadRequestWithMinimalValues() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "user",
                "A",
                "B",
                true,
                LocalDate.of(2024, 3, 1),
                1,
                ActionType.ADD);

        TrainingSession result = mapper.toTrainingSession(request);

        assertNotNull(result);
        assertEquals(LocalDate.of(2024, 3, 1), result.getTrainingDate());
        assertEquals(1, result.getTrainingDuration());
        assertEquals(ActionType.ADD, result.getActionType());
    }

    @Test
    void shouldReturnNullWhenRequestIsNull() {
        TrainerWorkloadRequest request = null;

        TrainingSession result = mapper.toTrainingSession(request);

        assertNull(result);
    }
}