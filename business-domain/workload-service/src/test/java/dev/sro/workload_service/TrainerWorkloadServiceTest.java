package dev.sro.workload_service;

import dev.sro.workload_service.application.dto.request.TrainerWorkloadRequest;
import dev.sro.workload_service.application.dto.response.TrainerMonthlySummaryResponse;
import dev.sro.workload_service.application.service.TrainerWorkloadService;
import dev.sro.workload_service.domain.enums.ActionType;
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
        TrainerWorkloadRequest request = TrainerWorkloadRequest.builder()
            .trainerUsername("john.doe")
            .trainerFirstName("John")
            .trainerLastName("Doe")
            .isActive(true)
            .trainingDate(LocalDate.of(2024, 1, 15))
            .trainingDuration(60)
            .actionType(ActionType.ADD)
            .build();
        
        // Act
        trainerWorkloadService.processTrainerWorkload(request);
        
        // Assert
        TrainerMonthlySummaryResponse response = trainerWorkloadService
            .getTrainerMonthlySummary("john.doe", 2024, 1);
        
        assertNotNull(response);
        assertEquals("john.doe", response.getTrainerUsername());
        assertEquals("John", response.getTrainerFirstName());
        assertEquals("Doe", response.getTrainerLastName());
        assertTrue(response.isTrainerStatus());
        assertEquals(1, response.getYears().size());
        assertEquals(2024, response.getYears().get(0).getYear());
        assertEquals(1, response.getYears().get(0).getMonths().size());
        assertEquals(1, response.getYears().get(0).getMonths().get(0).getMonth());
        assertEquals(60, response.getYears().get(0).getMonths().get(0).getTrainingSummaryDuration());
    }
    
    @Test
    public void testProcessTrainerWorkloadWithDelete() {
        // Arrange - First add a training
        TrainerWorkloadRequest addRequest = TrainerWorkloadRequest.builder()
            .trainerUsername("jane.smith")
            .trainerFirstName("Jane")
            .trainerLastName("Smith")
            .isActive(true)
            .trainingDate(LocalDate.of(2024, 2, 10))
            .trainingDuration(90)
            .actionType(ActionType.ADD)
            .build();
        
        trainerWorkloadService.processTrainerWorkload(addRequest);
        
        // Act - Delete some training time
        TrainerWorkloadRequest deleteRequest = TrainerWorkloadRequest.builder()
            .trainerUsername("jane.smith")
            .trainerFirstName("Jane")
            .trainerLastName("Smith")
            .isActive(true)
            .trainingDate(LocalDate.of(2024, 2, 10))
            .trainingDuration(30)
            .actionType(ActionType.DELETE)
            .build();
        
        trainerWorkloadService.processTrainerWorkload(deleteRequest);
        
        // Assert
        TrainerMonthlySummaryResponse response = trainerWorkloadService
            .getTrainerMonthlySummary("jane.smith", 2024, 2);
        
        assertNotNull(response);
        assertEquals("jane.smith", response.getTrainerUsername());
        assertEquals(1, response.getYears().size());
        assertEquals(2024, response.getYears().get(0).getYear());
        assertEquals(1, response.getYears().get(0).getMonths().size());
        assertEquals(2, response.getYears().get(0).getMonths().get(0).getMonth());
        assertEquals(60, response.getYears().get(0).getMonths().get(0).getTrainingSummaryDuration());
    }
} 