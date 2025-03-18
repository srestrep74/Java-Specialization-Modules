package com.sro.SpringCoreTask1.util.menus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.sro.SpringCoreTask1.dto.TraineeTrainingFilterDTO;
import com.sro.SpringCoreTask1.dto.request.TraineeRequestDTO;
import com.sro.SpringCoreTask1.dto.request.TrainingRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TraineeResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainerResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingTypeResponseDTO;
import com.sro.SpringCoreTask1.facade.SystemServiceFacade;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
public class TraineeMenu implements Menu {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final Scanner scanner;
    private final SystemServiceFacade facade;

    public TraineeMenu(SystemServiceFacade facade) {
        this.facade = facade;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void displayMenu() {
        System.out.println("\n===== Trainee Menu =====");
        for (MenuOption option : MenuOption.values()) {
            System.out.println(option.getOption() + ". " + option.getDescription());
        }
        System.out.print("Select an option: ");
    }

    @Override
    public boolean processOption(int choice) {
        if (!facade.isTraineeAuthenticated()) {
            System.out.println("You must be logged in as a trainee to access this menu.");
            return true;
        }

        Optional<MenuOption> selectedOption = MenuOption.fromOption(choice);
        if (selectedOption.isEmpty()) {
            System.out.println("Invalid option. Please try again.");
            return true;
        }

        switch (selectedOption.get()) {
            case VIEW_PROFILE -> viewProfile();
            case UPDATE_PROFILE -> updateProfile();
            case CHANGE_PASSWORD -> changePassword();
            case VIEW_AVAILABLE_TRAINERS -> viewAvailableTrainers();
            case VIEW_MY_TRAINERS -> viewMyTrainers();
            case VIEW_TRAININGS -> viewTrainings();
            case ADD_TRAINING -> addTraining();
            case TOGGLE_PROFILE_STATUS -> toggleProfileStatus();
            case DELETE_PROFILE -> deleteProfile();
            case UPDATE_TRAINERS_LIST -> updateTrainersList();
            case BACK_TO_MAIN_MENU -> {
                return false;
            }
        }
        return true;
    }

    private Long getTraineeId() {
        return facade.getCurrentTraineeId();
    }

    private void viewProfile() {
        TraineeResponseDTO trainee = facade.getTraineeById(getTraineeId());
        System.out.println("\n===== Trainee Profile =====");
        System.out.println("ID: " + trainee.id());
        System.out.println("First Name: " + trainee.firstName());
        System.out.println("Last Name: " + trainee.lastName());
        System.out.println("Username: " + trainee.username());
        System.out.println("Address: " + trainee.address());
        System.out.println("Date of Birth: " + trainee.dateOfBirth().format(DATE_FORMATTER));
        System.out.println("Status: " + (trainee.active() ? "Active" : "Inactive"));
    }

    private void updateProfile() {
        TraineeResponseDTO trainee = facade.getTraineeById(getTraineeId());

        System.out.println("\n===== Update Trainee Profile =====");
        System.out.println("Enter new details (press Enter to keep current values):");

        String firstName = getInput("First Name [" + trainee.firstName() + "]: ", trainee.firstName());
        String lastName = getInput("Last Name [" + trainee.lastName() + "]: ", trainee.lastName());
        String address = getInput("Address [" + trainee.address() + "]: ", trainee.address());
        LocalDate dateOfBirth = getDateInput("Date of Birth [" + trainee.dateOfBirth() + "] (yyyy-MM-dd): ", trainee.dateOfBirth());
        TraineeRequestDTO requestDTO = new TraineeRequestDTO(firstName, lastName, trainee.username(), null, trainee.active(), address, dateOfBirth, null);
        TraineeResponseDTO updatedTrainee = facade.updateTraineeProfile(requestDTO);

        System.out.println("Profile updated successfully!");
        System.out.println("Updated Trainee ID: " + updatedTrainee.id());
    }

    private void changePassword() {
        System.out.println("\n----- Change Password -----");
        String newPassword = getInput("Enter new password: ", "");
        if (newPassword.isEmpty()) {
            System.out.println("Password cannot be empty.");
            return;
        }
        TraineeResponseDTO trainee = facade.getTraineeById(getTraineeId());
        facade.changeTraineePassword(trainee.id(), newPassword);
        System.out.println("Password changed successfully!");
    }
    private void viewAvailableTrainers() {
        System.out.println("\n----- Available Trainers -----");
        TraineeResponseDTO trainee = facade.getTraineeById(getTraineeId());
        List<TrainerResponseDTO> availableTrainers = facade.getTrainersNotAssignedToTrainee(trainee.username());
    
        if (availableTrainers.isEmpty()) {
            System.out.println("No available trainers found.");
            return;
        }
    
        System.out.printf("%-5s | %-20s | %-15s | %-10s%n", "ID", "Name", "Training Type", "Status");
        System.out.println("-----------------------------------------------------");
        availableTrainers.forEach(trainer -> {
            String details = String.format("%-5s | %-20s | %-15s | %-10s",
                    trainer.id(),
                    trainer.firstName() + " " + trainer.lastName(),
                    trainer.trainingType().trainingTypeName(),
                    trainer.active() ? "Active" : "Inactive");
            System.out.println(details);
        });
    }

    private void viewMyTrainers() {
        System.out.println("\n----- Available Trainers -----");
        Set<TrainerResponseDTO> availableTrainers = facade.getTraineeTrainers(getTraineeId());
    
        if (availableTrainers.isEmpty()) {
            System.out.println("No available trainers found.");
            return;
        }
    
        System.out.printf("%-5s | %-20s | %-15s | %-10s%n", "ID", "Name", "Training Type", "Status");
        System.out.println("-----------------------------------------------------");
        availableTrainers.forEach(trainer -> {
            String details = String.format("%-5s | %-20s | %-15s | %-10s",
                    trainer.id(),
                    trainer.firstName() + " " + trainer.lastName(),
                    trainer.trainingType().trainingTypeName(),
                    trainer.active() ? "Active" : "Inactive");
            System.out.println(details);
        });
    }

    private void toggleProfileStatus() {
        System.out.println("\n----- Toggle Profile Status -----");
        boolean currentStatus = facade.getTraineeById(getTraineeId()).active();
        String newStatusText = currentStatus ? "inactive" : "active";

        System.out.println("\nYour profile is currently " + (currentStatus ? "active" : "inactive") + ".");
        String response = getInput("Do you want to make it " + newStatusText + "? (Y/N): ", "").toUpperCase();

        if (response.equals("Y")) {
            facade.setTraineeStatus(getTraineeId(), !currentStatus);
            System.out.println("Profile status updated successfully! Your profile is now " + newStatusText + ".");
        } else {
            System.out.println("Profile status remains unchanged.");
        }
    }

    private void deleteProfile() {
        System.out.println("\n----- Delete Profile -----");
        System.out.println("WARNING: This action cannot be undone!");
        String confirmation = getInput("Are you sure you want to delete your profile? (type 'DELETE' to confirm): ", "");

        if (confirmation.equals("DELETE")) {
            TraineeResponseDTO trainee = facade.getTraineeById(getTraineeId());
            facade.deleteTraineeProfileByUsername(trainee.username());
            System.out.println("Profile deleted successfully.");
            facade.logout();
        } else {
            System.out.println("Profile deletion cancelled.");
        }
    }

    private String getInput(String prompt, String defaultValue) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }

    private LocalDate getDateInput(String prompt, LocalDate defaultValue) {
        System.out.print(prompt);
        String dateStr = scanner.nextLine().trim();
        if (dateStr.isEmpty()) {
            return defaultValue;
        }
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Using current date of birth.");
            return defaultValue;
        }
    }

    private void viewTrainings() {
        System.out.println("\n===== Your Trainings =====");
        TraineeTrainingFilterDTO filterDTO = getTrainingFilters();
        TraineeResponseDTO trainee = facade.getTraineeById(getTraineeId());
        List<TrainingResponseDTO> trainings = facade.getTraineeTrainingsList(trainee.username(), filterDTO);

        if (trainings.isEmpty()) {
            System.out.println("No trainings found with the specified filters.");
            return;
        }
        System.out.printf("%-5s | %-20s | %-15s | %-10s | %-15s%n", "ID", "Training Name", "Date", "Duration", "Trainer");
        System.out.println("---------------------------------------------------------------");
        trainings.forEach(this::printTrainingDetails);
    }

    private void printTrainingDetails(TrainingResponseDTO training) {
        String details = String.format("%-5s | %-20s | %-15s | %-10s | %-15s",
                training.id(),
                training.trainingName(),
                training.trainingDate().format(DATE_FORMATTER),
                training.duration() + " mins",
                training.trainer().firstName() + " " + training.trainer().lastName());
        System.out.println(details);
    }

    private TraineeTrainingFilterDTO getTrainingFilters() {
        System.out.println("\n===== Enter Training Filters =====");
        Long traineeId = getTraineeId(); 

        System.out.print("Enter start date (YYYY-MM-DD, leave empty for no filter): ");
        LocalDate fromDate = getDateInput("", null);

        System.out.print("Enter end date (YYYY-MM-DD, leave empty for no filter): ");
        LocalDate toDate = getDateInput("", null);

        System.out.print("Enter trainer name (leave empty for no filter): ");
        String trainerName = scanner.nextLine().trim();

        System.out.print("Enter training type (leave empty for no filter): ");
        String trainingType = scanner.nextLine().trim();
        return new TraineeTrainingFilterDTO(traineeId, fromDate, toDate, trainerName, trainingType);
    }

    private void addTraining() {
        System.out.println("\n===== Add Training =====");
        viewMyTrainers();

        System.out.print("Enter trainer ID: ");
        Long trainerId = Long.parseLong(scanner.nextLine().trim());

        List<TrainingTypeResponseDTO> trainingTypes = facade.getTrainingTypes();
        System.out.println("\n----- Available Training Types -----");
        trainingTypes.forEach(trainingType -> System.out.println(trainingType.id() + ": " + trainingType.trainingTypeName()));

        System.out.print("Enter training type ID: ");
        Long trainingTypeId = Long.parseLong(scanner.nextLine().trim());

        System.out.print("Enter training name: ");
        String trainingName = scanner.nextLine().trim();

        LocalDate trainingDate = getDateInput("Enter training date (YYYY-MM-DD): ", null);

        System.out.print("Enter training duration (in minutes): ");
        int duration = Integer.parseInt(scanner.nextLine().trim());

        Long traineeId = getTraineeId(); 

        TrainingRequestDTO trainingRequestDTO = new TrainingRequestDTO(trainingName, trainingDate, duration, traineeId, trainerId, trainingTypeId);
        TrainingResponseDTO savedTraining = facade.addTraining(trainingRequestDTO);
        System.out.println("Training added successfully!");
        System.out.println("Training ID: " + savedTraining.id());
    }

    private void updateTrainersList() {
        System.out.println("\n===== Update Trainers List =====");
        Long traineeId = getTraineeId();

        System.out.print("Do you want to add or remove a trainer? (add/remove): ");
        String action = scanner.nextLine().trim().toLowerCase();
    
        if (!action.equals("add") && !action.equals("remove")) {
            System.out.println("Invalid action. Please enter 'add' or 'remove'.");
            return;
        }

        if (action.equals("add")) {
            System.out.println("\n----- Available Trainers -----");
            viewAvailableTrainers();
        } else {
            System.out.println("\n----- Your Assigned Trainers -----");
            Set<TrainerResponseDTO> assignedTrainers = facade.getTraineeTrainers(traineeId);
    
            if (assignedTrainers.isEmpty()) {
                System.out.println("No trainers assigned to you.");
                return;
            }
            System.out.printf("%-5s | %-20s | %-15s | %-10s%n", "ID", "Name", "Training Type", "Status");
            System.out.println("-----------------------------------------------------");
            assignedTrainers.forEach(trainer -> {
                String details = String.format("%-5s | %-20s | %-15s | %-10s",
                        trainer.id(),
                        trainer.firstName() + " " + trainer.lastName(),
                        trainer.trainingType().trainingTypeName(),
                        trainer.active() ? "Active" : "Inactive");
                System.out.println(details);
            });
        }
        System.out.print("Enter trainer ID: ");
        Long trainerId = Long.parseLong(scanner.nextLine().trim());
    
        facade.updateTraineeTrainersList(traineeId, trainerId, action);
        System.out.println("Trainer list updated successfully!");
    }

    @Getter
    @RequiredArgsConstructor
    private enum MenuOption {
        VIEW_PROFILE(1, "View Profile"),
        UPDATE_PROFILE(2, "Update Profile"),
        CHANGE_PASSWORD(3, "Change Password"),
        VIEW_AVAILABLE_TRAINERS(4, "View Available Trainers"),
        VIEW_TRAININGS(5, "View Trainings"),
        VIEW_MY_TRAINERS(6, "View My Trainers"),
        ADD_TRAINING(7, "Add Training"),
        TOGGLE_PROFILE_STATUS(8, "Activate/Deactivate Profile"),
        DELETE_PROFILE(9, "Delete Profile"),
        UPDATE_TRAINERS_LIST(10, "Update Trainers List"),
        BACK_TO_MAIN_MENU(0, "Back to Main Menu");

        private final int option;
        private final String description;
        
        public static Optional<MenuOption> fromOption(int option) {
            for (MenuOption menuOption : values()) {
                if (menuOption.option == option) {
                    return Optional.of(menuOption);
                }
            }
            return Optional.empty();
        }
    }
}