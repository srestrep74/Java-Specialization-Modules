package com.sro.SpringCoreTask1.repository.specification;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.sro.SpringCoreTask1.entity.Training;

public class TrainingSpecifications {

    public static Specification<Training> hasTraineeUsername(String username) {
        return (root, query,
                cb) -> StringUtils.hasText(username) ? cb.equal(root.get("trainee").get("username"), username) : null;
    }

    public static Specification<Training> hasTrainerUsername(String username) {
        return (root, query,
                cb) -> StringUtils.hasText(username) ? cb.equal(root.get("trainer").get("username"), username) : null;
    }

    public static Specification<Training> dateAfterOrEqual(LocalDate date) {
        return (root, query, cb) -> date != null ? cb.greaterThanOrEqualTo(root.get("trainingDate"), date) : null;
    }

    public static Specification<Training> dateBeforeOrEqual(LocalDate date) {
        return (root, query, cb) -> date != null ? cb.lessThanOrEqualTo(root.get("trainingDate"), date) : null;
    }

    public static Specification<Training> trainerUsernameContains(String username) {
        return (root, query, cb) -> StringUtils.hasText(username)
                ? cb.like(root.get("trainer").get("username"), "%" + username.toLowerCase() + "%")
                : null;
    }

    public static Specification<Training> traineeUsernameContains(String username) {
        return (root, query, cb) -> StringUtils.hasText(username)
                ? cb.like(root.get("trainee").get("username"), "%" + username.toLowerCase() + "%")
                : null;
    }

    public static Specification<Training> hasTrainingType(String trainingType) {
        return (root, query, cb) -> StringUtils.hasText(trainingType)
                ? cb.equal(root.get("trainingType").get("trainingTypeName"), trainingType)
                : null;
    }

    public static Specification<Training> withTraineeAndTrainerFetched() {
        return (root, query, cb) -> {
            if (query != null && !Long.class.equals(query.getResultType())) {
                root.fetch("trainee");
                root.fetch("trainer");
                root.fetch("trainingType");
            }
            return null;
        };
    }

}
