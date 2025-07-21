package dev.sro.workload_service.mapper;

import dev.sro.workload_service.dtos.v1.request.TrainerWorkloadRequest;
import dev.sro.workload_service.entity.Trainer;
import dev.sro.workload_service.entity.enums.ActionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TrainerMapperTest {

    @Autowired
    private TrainerMapper mapper;

    @Test
    void shouldMapTrainerWorkloadRequestToTrainer() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "trainer.username",
                "John",
                "Doe",
                true,
                LocalDate.of(2024, 1, 15),
                60,
                ActionType.ADD);

        Trainer result = mapper.toTrainer(request);

        assertNotNull(result);
        assertEquals("trainer.username", result.getUsername());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals(true, result.getIsActive());
        assertTrue(result.getTrainingSessions().isEmpty());
        assertTrue(result.getMonthlySummaries().isEmpty());
    }

    @Test
    void shouldMapTrainerWorkloadRequestWithInactiveStatus() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "inactive.trainer",
                "Jane",
                "Smith",
                false,
                LocalDate.of(2024, 2, 20),
                90,
                ActionType.DELETE);

        Trainer result = mapper.toTrainer(request);

        assertNotNull(result);
        assertEquals("inactive.trainer", result.getUsername());
        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals(false, result.getIsActive());
    }

    @Test
    void shouldMapTrainerWorkloadRequestWithSpecialCharacters() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "user.name-123",
                "José María",
                "García-López",
                true,
                LocalDate.of(2024, 3, 1),
                120,
                ActionType.ADD);

        Trainer result = mapper.toTrainer(request);

        assertNotNull(result);
        assertEquals("user.name-123", result.getUsername());
        assertEquals("José María", result.getFirstName());
        assertEquals("García-López", result.getLastName());
        assertEquals(true, result.getIsActive());
    }

    @Test
    void shouldMapTrainerWorkloadRequestWithMinimalValues() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "u",
                "A",
                "B",
                true,
                LocalDate.of(2024, 4, 1),
                1,
                ActionType.ADD);

        Trainer result = mapper.toTrainer(request);

        assertNotNull(result);
        assertEquals("u", result.getUsername());
        assertEquals("A", result.getFirstName());
        assertEquals("B", result.getLastName());
        assertEquals(true, result.getIsActive());
    }

    @Test
    void shouldReturnNullWhenRequestIsNull() {
        TrainerWorkloadRequest request = null;

        Trainer result = mapper.toTrainer(request);

        assertNull(result);
    }
}