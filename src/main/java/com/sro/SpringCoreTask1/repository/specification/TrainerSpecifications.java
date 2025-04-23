package com.sro.SpringCoreTask1.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

public class TrainerSpecifications {

    public static Specification<Trainer> isActive() {
        return (root, query, cb) -> cb.equal(root.get("active"), true);
    }

    public static Specification<Trainer> notAssignedToTrainee(String traineeUsername) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(traineeUsername)) {
                return null;
            }

            if (query != null) {
                query.distinct(true);
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<Trainer> subRoot = subquery.from(Trainer.class);
                Join<Trainer, Trainee> assignedTrainees = subRoot.join("trainees", JoinType.INNER);

                subquery.select(subRoot.get("id"))
                        .where(cb.equal(assignedTrainees.get("username"), traineeUsername));

                return cb.not(root.get("id").in(subquery));
            }
            return null;
        };
    }

    public static Specification<Trainer> withTrainingTypeFetched() {
        return (root, query, cb) -> {
            if (query != null && Long.class.equals(query.getResultType())) {
                root.fetch("trainingType", JoinType.LEFT);
            }
            return null;
        };
    }

}
