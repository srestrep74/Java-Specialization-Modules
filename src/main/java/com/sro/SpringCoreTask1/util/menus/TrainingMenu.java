package com.sro.SpringCoreTask1.util.menus;

import com.sro.SpringCoreTask1.dto.TraineeDTO;
import com.sro.SpringCoreTask1.dto.TrainerDTO;
import com.sro.SpringCoreTask1.dto.TrainingDTO;
import com.sro.SpringCoreTask1.models.TrainingType;
import com.sro.SpringCoreTask1.facade.TrainingFacade;
import com.sro.SpringCoreTask1.models.id.TrainingId;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Duration;
import java.util.Scanner;

@Component
public class TrainingMenu {

    private final TrainingFacade trainingFacade;

    public TrainingMenu(TrainingFacade trainingFacade) {
        this.trainingFacade = trainingFacade;
    }

    public void showMenu(Scanner scanner) {
        boolean back = false;

        while (!back) {
            System.out.println("=== TRAINING MENU ===");
            System.out.println("1. Create Training");
            System.out.println("2. Find Training by ID");
            System.out.println("3. List All Trainings");
            System.out.println("4. Update Training");
            System.out.println("5. Delete Training");
            System.out.println("6. Return to Main Menu");
            System.out.print("Select an option: ");

            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    createTraining(scanner);
                    break;
                case 2:
                    findTrainingById(scanner);
                    break;
                case 3:
                    listAllTrainings();
                    break;
                case 4:
                    updateTraining(scanner);
                    break;
                case 5:
                    deleteTraining(scanner);
                    break;
                case 6:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void createTraining(Scanner scanner) {
        System.out.print("Enter Trainee ID: ");
        Long traineeId = scanner.nextLong();
        System.out.print("Enter Trainer ID: ");
        Long trainerId = scanner.nextLong();
        scanner.nextLine();

        System.out.print("Enter training name: ");
        String trainingName = scanner.nextLine();
        System.out.print("Enter training date (yyyy-MM-dd): ");
        LocalDate trainingDate = LocalDate.parse(scanner.nextLine());
        System.out.print("Enter training duration (in hours): ");
        Duration duration = Duration.ofHours(scanner.nextLong());
        scanner.nextLine();

        System.out.print("Enter training type: ");
        String trainingTypeName = scanner.nextLine();

        TraineeDTO traineeDTO = trainingFacade.findTraineeById(traineeId);
        TrainerDTO trainerDTO = trainingFacade.findTrainerById(trainerId);

        TrainingDTO trainingDTO = new TrainingDTO();
        trainingDTO.setTrainingId(new TrainingId(traineeId, trainerId));
        trainingDTO.setTrainingName(trainingName);
        trainingDTO.setTrainingDate(trainingDate);
        trainingDTO.setDuration(duration);
        trainingDTO.setTrainee(traineeDTO);
        trainingDTO.setTrainer(trainerDTO);
        trainingDTO.setTrainingType(new TrainingType(trainingTypeName));

        TrainingDTO savedTraining = trainingFacade.saveTraining(trainingDTO);
        System.out.println("Training created: " + savedTraining);
    }

    private void findTrainingById(Scanner scanner) {
        System.out.print("Enter Trainee ID: ");
        Long traineeId = scanner.nextLong();
        System.out.print("Enter Trainer ID: ");
        Long trainerId = scanner.nextLong();
        scanner.nextLine();

        TrainingId trainingId = new TrainingId(traineeId, trainerId);
        TrainingDTO trainingDTO = trainingFacade.findTrainingById(trainingId);

        if (trainingDTO != null) {
            System.out.println("Training found: " + trainingDTO);
        } else {
            System.out.println("Training not found.");
        }
    }

    private void listAllTrainings() {
        System.out.println("List of Trainings:");
        trainingFacade.findAllTrainings().forEach(System.out::println);
    }

    private void updateTraining(Scanner scanner) {
        System.out.print("Enter Trainee ID: ");
        Long traineeId = scanner.nextLong();
        System.out.print("Enter Trainer ID: ");
        Long trainerId = scanner.nextLong();
        scanner.nextLine();

        TrainingId trainingId = new TrainingId(traineeId, trainerId);
        TrainingDTO trainingDTO = trainingFacade.findTrainingById(trainingId);

        if (trainingDTO != null) {
            System.out.print("Enter new training name: ");
            trainingDTO.setTrainingName(scanner.nextLine());
            System.out.print("Enter new training date (yyyy-MM-dd): ");
            trainingDTO.setTrainingDate(LocalDate.parse(scanner.nextLine()));
            System.out.print("Enter new training duration (in hours): ");
            trainingDTO.setDuration(Duration.ofHours(scanner.nextLong()));
            scanner.nextLine();
            System.out.print("Enter new training type: ");
            TrainingType trainingType = new TrainingType(scanner.nextLine());
            trainingDTO.setTrainingType(trainingType);

            TrainingDTO updatedTraining = trainingFacade.updateTraining(trainingDTO);
            System.out.println("Training updated: " + updatedTraining);
        } else {
            System.out.println("Training not found.");
        }
    }

    private void deleteTraining(Scanner scanner) {
        System.out.print("Enter Trainee ID: ");
        Long traineeId = scanner.nextLong();
        System.out.print("Enter Trainer ID: ");
        Long trainerId = scanner.nextLong();
        scanner.nextLine();

        TrainingId trainingId = new TrainingId(traineeId, trainerId);
        trainingFacade.deleteTraining(trainingId);
        System.out.println("Training deleted.");
    }
}