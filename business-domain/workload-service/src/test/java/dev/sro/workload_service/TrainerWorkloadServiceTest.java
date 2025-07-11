package dev.sro.workload_service;

import dev.sro.workload_service.dtos.v1.request.TrainerWorkloadRequest;
import dev.sro.workload_service.dtos.v1.response.TrainerMonthlySummaryResponse;
import dev.sro.workload_service.entity.enums.ActionType;
import dev.sro.workload_service.service.TrainerWorkloadService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class TrainerWorkloadServiceTest {
    
    @Autowired
    private TrainerWorkloadService trainerWorkloadService;
    
    @Test
    public void testProcessTrainerWorkload() {
        // Arrange
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
            "john.doe",
            "John",
            "Doe",
            true,
            LocalDate.of(2024, 1, 15),
            60,
            ActionType.ADD
        );
        
        // Act
        trainerWorkloadService.processTrainerWorkload(request);
        
        // Assert
        TrainerMonthlySummaryResponse response = trainerWorkloadService
            .getTrainerMonthlySummary("john.doe", 2024, 1);
        
        assertNotNull(response);
        assertEquals("john.doe", response.trainerUsername());
        assertEquals("John", response.trainerFirstName());
        assertEquals("Doe", response.trainerLastName());
        assertTrue(response.trainerStatus());
        assertEquals(1, response.years().size());
        assertEquals(2024, response.years().get(0).year());
        assertEquals(1, response.years().get(0).months().size());
        assertEquals(1, response.years().get(0).months().get(0).month());
        assertEquals(60, response.years().get(0).months().get(0).trainingSummaryDuration());
    }
    
    @Test
    public void testProcessTrainerWorkloadWithDelete() {
        // Arrange - First add a training
        TrainerWorkloadRequest addRequest = new TrainerWorkloadRequest(
            "jane.smith",
            "Jane",
            "Smith",
            true,
            LocalDate.of(2024, 2, 10),
            90,
            ActionType.ADD
        );
        
        trainerWorkloadService.processTrainerWorkload(addRequest);
        
        // Act - Delete some training time
        TrainerWorkloadRequest deleteRequest = new TrainerWorkloadRequest(
            "jane.smith",
            "Jane",
            "Smith",
            true,
            LocalDate.of(2024, 2, 10),
            30,
            ActionType.DELETE
        );
        
        trainerWorkloadService.processTrainerWorkload(deleteRequest);
        
        // Assert
        TrainerMonthlySummaryResponse response = trainerWorkloadService
            .getTrainerMonthlySummary("jane.smith", 2024, 2);
        
        assertNotNull(response);
        assertEquals("jane.smith", response.trainerUsername());
        assertEquals(1, response.years().size());
        assertEquals(2024, response.years().get(0).year());
        assertEquals(1, response.years().get(0).months().size());
        assertEquals(2, response.years().get(0).months().get(0).month());
        assertEquals(60, response.years().get(0).months().get(0).trainingSummaryDuration());
    }
} 