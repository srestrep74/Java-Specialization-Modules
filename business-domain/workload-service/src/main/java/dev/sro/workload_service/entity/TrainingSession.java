package dev.sro.workload_service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;

import dev.sro.workload_service.entity.enums.ActionType;

@Document(collection = "training_sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingSession {
    
    @Id
    private String id;
    
    @Indexed
    @Field("trainer_username")
    private String trainerUsername;
    
    @Field("training_date")
    private LocalDate trainingDate;
    
    @Field("training_duration")
    private Integer trainingDuration; // in minutes
    
    @Field("action_type")
    private ActionType actionType;
    
    @Field("created_at")
    private LocalDateTime createdAt;
    
    @Field("updated_at")
    private LocalDateTime updatedAt;
    
    public int getYear() {
        return trainingDate.getYear();
    }
    
    public int getMonth() {
        return trainingDate.getMonthValue();
    }
} 