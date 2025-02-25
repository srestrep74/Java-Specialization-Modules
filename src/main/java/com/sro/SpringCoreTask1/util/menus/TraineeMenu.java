package com.sro.SpringCoreTask1.util.menus;

import com.sro.SpringCoreTask1.dto.TraineeDTO;
import com.sro.SpringCoreTask1.facade.TrainingFacade;
import com.sro.SpringCoreTask1.util.IdGenerator;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Scanner;

@Component
public class TraineeMenu {

    private final TrainingFacade trainingFacade;

    public TraineeMenu(TrainingFacade trainingFacade) {
        this.trainingFacade = trainingFacade;
    }

    public void showMenu(Scanner scanner) {
        boolean back = false;

        while (!back) {
            System.out.println("=== TRAINEE MENU ===");
            System.out.println("1. Create Trainee");
            System.out.println("2. Find Trainee by ID");
            System.out.println("3. List All Trainees");
            System.out.println("4. Update Trainee");
            System.out.println("5. Delete Trainee");
            System.out.println("6. Return to Main Menu");
            System.out.print("Select an option: ");

            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    createTrainee(scanner);
                    break;
                case 2:
                    findTraineeById(scanner);
                    break;
                case 3:
                    listAllTrainees();
                    break;
                case 4:
                    updateTrainee(scanner);
                    break;
                case 5:
                    deleteTrainee(scanner);
                    break;
                case 6:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void createTrainee(Scanner scanner) {
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();
        System.out.print("Enter address: ");
        String address = scanner.nextLine();
        System.out.print("Enter date of birth (yyyy-MM-dd): ");
        LocalDate dateOfBirth = LocalDate.parse(scanner.nextLine());

        TraineeDTO traineeDTO = new TraineeDTO();
        traineeDTO.setUserId(IdGenerator.generateId());
        traineeDTO.setFirstName(firstName);
        traineeDTO.setLastName(lastName);
        traineeDTO.setAddress(address);
        traineeDTO.setDateOfBirth(dateOfBirth);

        TraineeDTO savedTrainee = trainingFacade.saveTrainee(traineeDTO);
        System.out.println("Trainee created: " + savedTrainee);
    }

    private void findTraineeById(Scanner scanner) {
        System.out.print("Enter Trainee ID: ");
        Long id = scanner.nextLong();
        scanner.nextLine();

        TraineeDTO traineeDTO = trainingFacade.findTraineeById(id);
        if (traineeDTO != null) {
            System.out.println("Trainee found: " + traineeDTO);
        } else {
            System.out.println("Trainee not found.");
        }
    }

    private void listAllTrainees() {
        System.out.println("List of Trainees:");
        trainingFacade.findAllTrainees().forEach(System.out::println);
    }

    private void updateTrainee(Scanner scanner) {
        System.out.print("Enter Trainee ID to update: ");
        Long id = scanner.nextLong();
        scanner.nextLine();

        TraineeDTO traineeDTO = trainingFacade.findTraineeById(id);
        if (traineeDTO != null) {
            System.out.print("Enter new first name: ");
            traineeDTO.setFirstName(scanner.nextLine());
            System.out.print("Enter new last name: ");
            traineeDTO.setLastName(scanner.nextLine());
            System.out.print("Enter new address: ");
            traineeDTO.setAddress(scanner.nextLine());
            System.out.print("Enter new date of birth (yyyy-MM-dd): ");
            traineeDTO.setDateOfBirth(LocalDate.parse(scanner.nextLine()));

            TraineeDTO updatedTrainee = trainingFacade.updateTrainee(traineeDTO);
            System.out.println("Trainee updated: " + updatedTrainee);
        } else {
            System.out.println("Trainee not found.");
        }
    }

    private void deleteTrainee(Scanner scanner) {
        System.out.print("Enter Trainee ID to delete: ");
        Long id = scanner.nextLong();
        scanner.nextLine();

        trainingFacade.deleteTrainee(id);
        System.out.println("Trainee deleted.");
    }
}