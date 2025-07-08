package dev.sro.gym_service.dtos.v1.request.workload;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerWorkloadRequest {
    
    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private Boolean isActive;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate trainingDate;
    
    private Integer trainingDuration; // in minutes
    private ActionType actionType;
    
    public enum ActionType {
        ADD,
        DELETE
    }
} 