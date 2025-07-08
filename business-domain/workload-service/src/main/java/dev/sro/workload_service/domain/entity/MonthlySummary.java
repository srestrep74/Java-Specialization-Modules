package dev.sro.workload_service.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "monthly_summaries", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"trainer_username", "year", "month"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySummary {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_username", nullable = false)
    private Trainer trainer;
    
    @Column(name = "year", nullable = false)
    private Integer year;
    
    @Column(name = "month", nullable = false)
    private Integer month;
    
    @Column(name = "total_duration", nullable = false)
    @Builder.Default
    private Integer totalDuration = 0; // in minutes
    
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
    
    public void addDuration(Integer duration) {
        this.totalDuration += duration;
    }
    
    public void subtractDuration(Integer duration) {
        this.totalDuration = Math.max(0, this.totalDuration - duration);
    }
} 