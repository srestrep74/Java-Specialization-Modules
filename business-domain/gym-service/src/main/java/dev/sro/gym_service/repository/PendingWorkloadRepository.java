package dev.sro.gym_service.repository;

import dev.sro.gym_service.entity.PendingWorkload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingWorkloadRepository extends JpaRepository<PendingWorkload, Long>, JpaSpecificationExecutor<PendingWorkload> {
} 