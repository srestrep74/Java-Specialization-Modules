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

import dev.sro.gym_service.entity.Trainee;
import dev.sro.gym_service.entity.Trainer;
import dev.sro.gym_service.entity.Training;
import dev.sro.gym_service.entity.TrainingType;

@DataJpaTest
@ActiveProfiles("test")
class TrainingTypeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    private TrainingType fitnessType;
    private TrainingType yogaType;
    private TrainingType cardioType;
    private Trainer trainer;
    private Trainee trainee;

    @BeforeEach
    void setUp() {
        fitnessType = createTrainingType("Fitness");
        yogaType = createTrainingType("Yoga");
        cardioType = createTrainingType("Cardio");

        trainer = createTrainer("John", "Doe", "johndoe", fitnessType);
        trainee = createTrainee("Jane", "Smith", "janesmith");
        createTraining(trainer, trainee, fitnessType);
    }

    private TrainingType createTrainingType(String name) {
        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName(name);
        return entityManager.persist(trainingType);
    }

    private Trainer createTrainer(String firstName, String lastName, String username, TrainingType trainingType) {
        Trainer trainer = new Trainer();
        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setUsername(username);
        trainer.setPassword("password");
        trainer.setActive(true);
        trainer.setTrainingType(trainingType);
        return entityManager.persist(trainer);
    }

    private Trainee createTrainee(String firstName, String lastName, String username) {
        Trainee trainee = new Trainee();
        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
        trainee.setUsername(username);
        trainee.setPassword("password");
        trainee.setActive(true);
        trainee.setDateOfBirth(LocalDate.of(1990, 5, 15));
        trainee.setAddress("123 Main St");
        return entityManager.persist(trainee);
    }

    private Training createTraining(Trainer trainer, Trainee trainee, TrainingType trainingType) {
        Training training = new Training();
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setTrainingType(trainingType);
        training.setTrainingName("Sample Training");
        training.setTrainingDate(LocalDate.now());
        training.setDuration(60);
        return entityManager.persist(training);
    }

    @Test
    void findById_ShouldReturnTrainingType_WhenExists() {
        Optional<TrainingType> found = trainingTypeRepository.findById(fitnessType.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTrainingTypeName()).isEqualTo("Fitness");
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        Optional<TrainingType> found = trainingTypeRepository.findById(999L);
        assertThat(found).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllTrainingTypes() {
        List<TrainingType> trainingTypes = trainingTypeRepository.findAll();
        assertThat(trainingTypes).hasSize(3)
                .contains(fitnessType, yogaType, cardioType);
    }

    @Test
    void save_ShouldPersistTrainingType() {
        TrainingType newType = new TrainingType();
        newType.setTrainingTypeName("Pilates");

        TrainingType saved = trainingTypeRepository.save(newType);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTrainingTypeName()).isEqualTo("Pilates");
    }

    @Test
    void save_ShouldUpdateExistingTrainingType() {
        fitnessType.setTrainingTypeName("Advanced Fitness");

        TrainingType updated = trainingTypeRepository.save(fitnessType);

        assertThat(updated.getTrainingTypeName()).isEqualTo("Advanced Fitness");
        assertThat(updated.getId()).isEqualTo(fitnessType.getId());
    }

    @Test
    void delete_ShouldRemoveTrainingType() {
        Long typeId = cardioType.getId();
        trainingTypeRepository.delete(cardioType);
        entityManager.flush();

        Optional<TrainingType> found = trainingTypeRepository.findById(typeId);
        assertThat(found).isEmpty();
    }

    @Test
    void deleteById_ShouldRemoveTrainingType() {
        Long typeId = yogaType.getId();
        trainingTypeRepository.deleteById(typeId);
        entityManager.flush();

        Optional<TrainingType> found = trainingTypeRepository.findById(typeId);
        assertThat(found).isEmpty();
    }

    @Test
    void findByTrainingTypeName_UsingSpecification() {
        Specification<TrainingType> spec = (root, query, cb) -> cb.equal(root.get("trainingTypeName"), "Fitness");

        List<TrainingType> trainingTypes = trainingTypeRepository.findAll(spec);

        assertThat(trainingTypes)
                .hasSize(1)
                .contains(fitnessType);
    }

    @Test
    void findByTrainingTypeNameContaining_UsingSpecification() {
        Specification<TrainingType> spec = (root, query, cb) -> cb.like(cb.lower(root.get("trainingTypeName")),
                "%fit%");

        List<TrainingType> trainingTypes = trainingTypeRepository.findAll(spec);

        assertThat(trainingTypes)
                .hasSize(1)
                .contains(fitnessType);
    }

    @Test
    void findTrainingTypesWithTrainers_UsingSpecification() {
        Specification<TrainingType> spec = (root, query, cb) -> cb.isNotEmpty(root.get("trainers"));

        List<TrainingType> trainingTypes = trainingTypeRepository.findAll(spec);

        assertThat(trainingTypes)
                .hasSize(1)
                .contains(fitnessType);
    }

    @Test
    void findTrainingTypesWithoutTrainers_UsingSpecification() {
        Specification<TrainingType> spec = (root, query, cb) -> cb.isEmpty(root.get("trainers"));

        List<TrainingType> trainingTypes = trainingTypeRepository.findAll(spec);

        assertThat(trainingTypes)
                .hasSize(2)
                .contains(yogaType, cardioType)
                .doesNotContain(fitnessType);
    }

    @Test
    void count_ShouldReturnCorrectCount() {
        long count = trainingTypeRepository.count();
        assertThat(count).isEqualTo(3);
    }

    @Test
    void existsById_ShouldReturnTrue_WhenExists() {
        boolean exists = trainingTypeRepository.existsById(fitnessType.getId());
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_ShouldReturnFalse_WhenNotExists() {
        boolean exists = trainingTypeRepository.existsById(999L);
        assertThat(exists).isFalse();
    }
}