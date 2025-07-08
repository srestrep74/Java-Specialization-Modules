package dev.sro.workload_service.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record TrainerMonthlySummaryResponse(
        String trainerUsername,
        String trainerFirstName,
        String trainerLastName,
        Boolean trainerStatus,
        List<YearSummary> years
) {
    public record YearSummary(
            int year,
            List<MonthSummary> months
    ) {}

    public record MonthSummary(
            int month,
            int trainingSummaryDuration
    ) {}
} 