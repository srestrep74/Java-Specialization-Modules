package dev.sro.workload_service.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.test.context.ActiveProfiles;

import dev.sro.workload_service.entity.Trainer;

@DataJpaTest
@ActiveProfiles("test")
@EntityScan(basePackages = "dev.sro.workload_service.entity")
class TrainerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TrainerRepository trainerRepository;

    private Trainer trainer1;
    private Trainer trainer2;

    @BeforeEach
    void setUp() {
        trainer1 = Trainer.builder()
                .username("john.doe")
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        entityManager.persist(trainer1);

        trainer2 = Trainer.builder()
                .username("jane.smith")
                .firstName("Jane")
                .lastName("Smith")
                .isActive(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        entityManager.persist(trainer2);

        entityManager.flush();
    }

    @Test
    @DisplayName("Should find trainer by username when trainer exists")
    void findByUsername_ShouldReturnTrainer_WhenTrainerExists() {
        Optional<Trainer> found = trainerRepository.findByUsername("john.doe");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("john.doe");
        assertThat(found.get().getFirstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("Should return empty when finding by non-existent username")
    void findByUsername_ShouldReturnEmpty_WhenTrainerDoesNotExist() {
        Optional<Trainer> found = trainerRepository.findByUsername("non.existent");

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should return true for existsByUsername when trainer exists")
    void existsByUsername_ShouldReturnTrue_WhenTrainerExists() {
        boolean exists = trainerRepository.existsByUsername("jane.smith");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false for existsByUsername when trainer does not exist")
    void existsByUsername_ShouldReturnFalse_WhenTrainerDoesNotExist() {
        boolean exists = trainerRepository.existsByUsername("non.existent");

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should save a new trainer correctly")
    void save_ShouldPersistNewTrainer() {
        Trainer newTrainer = Trainer.builder()
                .username("new.trainer")
                .firstName("New")
                .lastName("Trainer")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Trainer savedTrainer = trainerRepository.save(newTrainer);
        entityManager.flush();
        entityManager.clear();

        Trainer found = entityManager.find(Trainer.class, "new.trainer");

        assertThat(found).isNotNull();
        assertThat(found.getFirstName()).isEqualTo("New");
        assertThat(savedTrainer.getUsername()).isEqualTo("new.trainer");
    }
}