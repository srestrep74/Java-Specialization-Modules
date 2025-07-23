package dev.sro.workload_service.dtos.v1.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Builder
public record TrainerMonthlySummaryResponse(
        String trainerUsername,
        String trainerFirstName,
        String trainerLastName,
        Boolean trainerStatus,
        List<YearData> years
) {
    @Builder
    public record YearData(
            Integer year,
            List<MonthData> months
    ) {}

    @Builder
    public record MonthData(
            Integer month,
            Integer trainingsSummaryDuration
    ) {}
} 