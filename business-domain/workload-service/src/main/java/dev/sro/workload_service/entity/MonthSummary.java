package dev.sro.workload_service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthSummary {
    
    @Field("month")
    private Integer month;
    
    @Field("trainings_summary_duration")
    private Integer trainingsSummaryDuration; // in minutes
    
    public void addDuration(Integer duration) {
        this.trainingsSummaryDuration += duration;
    }
    
    public void subtractDuration(Integer duration) {
        this.trainingsSummaryDuration = Math.max(0, this.trainingsSummaryDuration - duration);
    }
} 