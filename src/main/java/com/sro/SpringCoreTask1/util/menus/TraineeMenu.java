package com.sro.SpringCoreTask1.util.menus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.sro.SpringCoreTask1.dto.request.TraineeRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TraineeResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainerResponseDTO;
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
            case TOGGLE_PROFILE_STATUS -> toggleProfileStatus();
            case DELETE_PROFILE -> deleteProfile();
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
        TraineeRequestDTO requestDTO = new TraineeRequestDTO(firstName, lastName, trainee.username(), null, trainee.active(), address, dateOfBirth);
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
        availableTrainers.forEach(this::printTrainerDetails);
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

    private void printTrainerDetails(TrainerResponseDTO trainer) {
        String details = String.format("%-5s | %-20s | %-15s | %-10s",
                trainer.id(),
                trainer.firstName() + " " + trainer.lastName(),
                trainer.trainingType().trainingTypeName(),
                trainer.active() ? "Active" : "Inactive");
        System.out.println(details);
    }

    @Getter
    @RequiredArgsConstructor
    private enum MenuOption {
        VIEW_PROFILE(1, "View Profile"),
        UPDATE_PROFILE(2, "Update Profile"),
        CHANGE_PASSWORD(3, "Change Password"),
        VIEW_AVAILABLE_TRAINERS(4, "View Available Trainers"),
        TOGGLE_PROFILE_STATUS(5, "Activate/Deactivate Profile"),
        DELETE_PROFILE(6, "Delete Profile"),
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