package dev.sro.workload_service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YearSummary {
    
    @Field("year")
    private Integer year;
    
    @Field("months")
    @Builder.Default
    private List<MonthSummary> months = new ArrayList<>();
    
    public MonthSummary findOrCreateMonth(Integer month) {
        return months.stream()
                .filter(m -> m.getMonth().equals(month))
                .findFirst()
                .orElseGet(() -> {
                    MonthSummary newMonth = MonthSummary.builder()
                            .month(month)
                            .trainingsSummaryDuration(0)
                            .build();
                    months.add(newMonth);
                    return newMonth;
                });
    }
} 