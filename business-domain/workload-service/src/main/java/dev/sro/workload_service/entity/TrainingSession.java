package dev.sro.workload_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import dev.sro.workload_service.entity.enums.ActionType;

@Entity
@Table(name = "training_sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_username", nullable = false)
    private Trainer trainer;
    
    @Column(name = "training_date", nullable = false)
    private LocalDate trainingDate;
    
    @Column(name = "training_duration", nullable = false)
    private Integer trainingDuration; // in minutes
    
    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private ActionType actionType;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public int getYear() {
        return trainingDate.getYear();
    }
    
    public int getMonth() {
        return trainingDate.getMonthValue();
    }
} 