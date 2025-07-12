package dev.sro.gym_service.entity;

import dev.sro.gym_service.entity.enums.ActionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "pending_workloads", uniqueConstraints = {
        // This constraint prevents duplicate entries from being created by the relay service.
        @UniqueConstraint(columnNames = {"trainerUsername", "trainingDate", "actionType"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingWorkload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String trainerUsername;

    @Column(nullable = false)
    private String trainerFirstname;

    @Column(nullable = false)
    private String trainerLastname;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private LocalDate trainingDate;

    @Column(nullable = false)
    private Integer trainingDuration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType actionType;

    @CreationTimestamp
    private LocalDateTime createdAt;
} 