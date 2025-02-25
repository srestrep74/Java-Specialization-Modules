package com.sro.SpringCoreTask1.util.menus;

import com.sro.SpringCoreTask1.dto.TrainerDTO;
import com.sro.SpringCoreTask1.facade.TrainingFacade;
import com.sro.SpringCoreTask1.models.TrainingType;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class TrainerMenu {

    private final TrainingFacade trainingFacade;

    public TrainerMenu(TrainingFacade trainingFacade) {
        this.trainingFacade = trainingFacade;
    }

    public void showMenu(Scanner scanner) {
        boolean back = false;

        while (!back) {
            System.out.println("=== TRAINER MENU ===");
            System.out.println("1. Create Trainer");
            System.out.println("2. Find Trainer by ID");
            System.out.println("3. List All Trainers");
            System.out.println("4. Update Trainer");
            System.out.println("5. Delete Trainer");
            System.out.println("6. Return to Main Menu");
            System.out.print("Select an option: ");

            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    createTrainer(scanner);
                    break;
                case 2:
                    findTrainerById(scanner);
                    break;
                case 3:
                    listAllTrainers();
                    break;
                case 4:
                    updateTrainer(scanner);
                    break;
                case 5:
                    deleteTrainer(scanner);
                    break;
                case 6:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void createTrainer(Scanner scanner) {
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();
        System.out.print("Enter specialization: ");
        String specialization = scanner.nextLine();
        System.out.print("Enter training type: ");
        String trainingTypeName = scanner.nextLine();

        TrainerDTO trainerDTO = new TrainerDTO();
        trainerDTO.setFirstName(firstName);
        trainerDTO.setLastName(lastName);
        trainerDTO.setSpecialization(specialization);
        trainerDTO.setTrainingType(new TrainingType(trainingTypeName));

        TrainerDTO savedTrainer = trainingFacade.saveTrainer(trainerDTO);
        System.out.println("Trainer created: " + savedTrainer);
    }

    private void findTrainerById(Scanner scanner) {
        System.out.print("Enter Trainer ID: ");
        Long id = scanner.nextLong();
        scanner.nextLine();

        TrainerDTO trainerDTO = trainingFacade.findTrainerById(id);
        if (trainerDTO != null) {
            System.out.println("Trainer found: " + trainerDTO);
        } else {
            System.out.println("Trainer not found.");
        }
    }

    private void listAllTrainers() {
        System.out.println("List of Trainers:");
        trainingFacade.findAllTrainers().forEach(System.out::println);
    }

    private void updateTrainer(Scanner scanner) {
        System.out.print("Enter Trainer ID to update: ");
        Long id = scanner.nextLong();
        scanner.nextLine();

        TrainerDTO trainerDTO = trainingFacade.findTrainerById(id);
        if (trainerDTO != null) {
            System.out.print("Enter new first name: ");
            trainerDTO.setFirstName(scanner.nextLine());
            System.out.print("Enter new last name: ");
            trainerDTO.setLastName(scanner.nextLine());
            System.out.print("Enter new specialization: ");
            trainerDTO.setSpecialization(scanner.nextLine());
            System.out.print("Enter new training type: ");
            String trainingTypeName = scanner.nextLine();
            trainerDTO.setTrainingType(new TrainingType(trainingTypeName));

            TrainerDTO updatedTrainer = trainingFacade.updateTrainer(trainerDTO);
            System.out.println("Trainer updated: " + updatedTrainer);
        } else {
            System.out.println("Trainer not found.");
        }
    }

    private void deleteTrainer(Scanner scanner) {
        System.out.print("Enter Trainer ID to delete: ");
        Long id = scanner.nextLong();
        scanner.nextLine();

        trainingFacade.deleteTrainer(id);
        System.out.println("Trainer deleted.");
    }
}