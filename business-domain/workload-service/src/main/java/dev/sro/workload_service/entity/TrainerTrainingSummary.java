package dev.sro.workload_service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "trainer_training_summaries")
@CompoundIndex(name = "trainer_name_idx", def = "{'trainer_first_name': 1, 'trainer_last_name': 1}")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerTrainingSummary {
    
    @Id
    private String id;
    
    @Field("trainer_username")
    private String trainerUsername;
    
    @Indexed
    @Field("trainer_first_name")
    private String trainerFirstName;
    
    @Indexed
    @Field("trainer_last_name")
    private String trainerLastName;
    
    @Field("trainer_status")
    private Boolean trainerStatus;
    
    @Field("years")
    @Builder.Default
    private List<YearSummary> years = new ArrayList<>();
    
    @Field("created_at")
    private LocalDateTime createdAt;
    
    @Field("updated_at")
    private LocalDateTime updatedAt;
    
    public void updateProfile(String firstName, String lastName, Boolean status) {
        this.trainerFirstName = firstName;
        this.trainerLastName = lastName;
        this.trainerStatus = status;
    }
    
    public YearSummary findOrCreateYear(Integer year) {
        return years.stream()
                .filter(y -> y.getYear().equals(year))
                .findFirst()
                .orElseGet(() -> {
                    YearSummary newYear = YearSummary.builder()
                            .year(year)
                            .months(new ArrayList<>())
                            .build();
                    years.add(newYear);
                    return newYear;
                });
    }
} 