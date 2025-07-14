package dev.sro.gym_service.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import dev.sro.gym_service.entity.PendingWorkload;
import dev.sro.gym_service.entity.enums.ActionType;

@DataJpaTest
@ActiveProfiles("test")
class PendingWorkloadRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PendingWorkloadRepository pendingWorkloadRepository;

    private PendingWorkload addWorkload;
    private PendingWorkload deleteWorkload;
    private PendingWorkload anotherAddWorkload;

    @BeforeEach
    void setUp() {
        addWorkload = createPendingWorkload("john.doe", "John", "Doe", true,
                LocalDate.of(2024, 1, 15), 60, ActionType.ADD);
        deleteWorkload = createPendingWorkload("jane.smith", "Jane", "Smith", true,
                LocalDate.of(2024, 1, 16), 90, ActionType.DELETE);
        anotherAddWorkload = createPendingWorkload("bob.wilson", "Bob", "Wilson", false,
                LocalDate.of(2024, 1, 17), 45, ActionType.ADD);
    }

    private PendingWorkload createPendingWorkload(String username, String firstName, String lastName,
            boolean isActive, LocalDate trainingDate, int duration, ActionType actionType) {
        PendingWorkload workload = PendingWorkload.builder()
                .trainerUsername(username)
                .trainerFirstname(firstName)
                .trainerLastname(lastName)
                .isActive(isActive)
                .trainingDate(trainingDate)
                .trainingDuration(duration)
                .actionType(actionType)
                .build();
        return entityManager.persist(workload);
    }

    @Test
    void findById_ShouldReturnPendingWorkload_WhenExists() {
        Optional<PendingWorkload> found = pendingWorkloadRepository.findById(addWorkload.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTrainerUsername()).isEqualTo("john.doe");
        assertThat(found.get().getActionType()).isEqualTo(ActionType.ADD);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        Optional<PendingWorkload> found = pendingWorkloadRepository.findById(999L);
        assertThat(found).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllPendingWorkloads() {
        List<PendingWorkload> workloads = pendingWorkloadRepository.findAll();
        assertThat(workloads).hasSize(3)
                .contains(addWorkload, deleteWorkload, anotherAddWorkload);
    }

    @Test
    void save_ShouldPersistPendingWorkload() {
        PendingWorkload newWorkload = PendingWorkload.builder()
                .trainerUsername("new.trainer")
                .trainerFirstname("New")
                .trainerLastname("Trainer")
                .isActive(true)
                .trainingDate(LocalDate.of(2024, 2, 1))
                .trainingDuration(120)
                .actionType(ActionType.ADD)
                .build();

        PendingWorkload saved = pendingWorkloadRepository.save(newWorkload);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTrainerUsername()).isEqualTo("new.trainer");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void delete_ShouldRemovePendingWorkload() {
        Long workloadId = addWorkload.getId();
        pendingWorkloadRepository.delete(addWorkload);
        entityManager.flush();

        Optional<PendingWorkload> found = pendingWorkloadRepository.findById(workloadId);
        assertThat(found).isEmpty();
    }

    @Test
    void findByTrainerUsername_UsingSpecification() {
        Specification<PendingWorkload> spec = (root, query, cb) -> cb.equal(root.get("trainerUsername"), "john.doe");

        List<PendingWorkload> workloads = pendingWorkloadRepository.findAll(spec);

        assertThat(workloads)
                .hasSize(1)
                .contains(addWorkload);
    }

    @Test
    void findByActionType_UsingSpecification() {
        Specification<PendingWorkload> spec = (root, query, cb) -> cb.equal(root.get("actionType"), ActionType.ADD);

        List<PendingWorkload> workloads = pendingWorkloadRepository.findAll(spec);

        assertThat(workloads)
                .hasSize(2)
                .contains(addWorkload, anotherAddWorkload)
                .doesNotContain(deleteWorkload);
    }

    @Test
    void findByTrainingDate_UsingSpecification() {
        LocalDate targetDate = LocalDate.of(2024, 1, 15);
        Specification<PendingWorkload> spec = (root, query, cb) -> cb.equal(root.get("trainingDate"), targetDate);

        List<PendingWorkload> workloads = pendingWorkloadRepository.findAll(spec);

        assertThat(workloads)
                .hasSize(1)
                .contains(addWorkload);
    }

    @Test
    void findByIsActive_UsingSpecification() {
        Specification<PendingWorkload> spec = (root, query, cb) -> cb.equal(root.get("isActive"), true);

        List<PendingWorkload> workloads = pendingWorkloadRepository.findAll(spec);

        assertThat(workloads)
                .hasSize(2)
                .contains(addWorkload, deleteWorkload)
                .doesNotContain(anotherAddWorkload);
    }

    @Test
    void findByTrainingDurationGreaterThan_UsingSpecification() {
        Specification<PendingWorkload> spec = (root, query, cb) -> cb.greaterThan(root.get("trainingDuration"), 50);

        List<PendingWorkload> workloads = pendingWorkloadRepository.findAll(spec);

        assertThat(workloads)
                .hasSize(2)
                .contains(addWorkload, deleteWorkload)
                .doesNotContain(anotherAddWorkload);
    }

    @Test
    void count_ShouldReturnCorrectCount() {
        long count = pendingWorkloadRepository.count();
        assertThat(count).isEqualTo(3);
    }

    @Test
    void existsById_ShouldReturnTrue_WhenExists() {
        boolean exists = pendingWorkloadRepository.existsById(addWorkload.getId());
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_ShouldReturnFalse_WhenNotExists() {
        boolean exists = pendingWorkloadRepository.existsById(999L);
        assertThat(exists).isFalse();
    }
}