package dev.sro.workload_service.repository;

import dev.sro.workload_service.entity.TrainerTrainingSummary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerTrainingSummaryRepository extends MongoRepository<TrainerTrainingSummary, String> {

    Optional<TrainerTrainingSummary> findByTrainerUsername(String trainerUsername);

    List<TrainerTrainingSummary> findByTrainerFirstNameAndTrainerLastName(String firstName, String lastName);

    List<TrainerTrainingSummary> findByTrainerFirstName(String firstName);

    List<TrainerTrainingSummary> findByTrainerLastName(String lastName);

    boolean existsByTrainerUsername(String trainerUsername);

    @Query("{'trainerUsername': ?0, 'years': {'$elemMatch': {'year': ?1, 'months': {'$elemMatch': {'month': ?2}}}}}")
    Optional<TrainerTrainingSummary> findByUsernameAndYearAndMonth(String trainerUsername, Integer year, Integer month);

}