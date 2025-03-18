package com.sro.SpringCoreTask1.util.menus;

import java.util.List;
import java.util.Scanner;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.sro.SpringCoreTask1.dto.request.TrainerRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainerResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingTypeResponseDTO;
import com.sro.SpringCoreTask1.facade.SystemServiceFacade;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
public class TrainerMenu implements Menu {

    private final Scanner scanner;
    private final SystemServiceFacade facade;

    public TrainerMenu(SystemServiceFacade facade) {
        this.facade = facade;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void displayMenu() {
        System.out.println("\n===== Trainer Menu =====");
        for (MenuOption option : MenuOption.values()) {
            System.out.println(option.getOption() + ". " + option.getDescription());
        }
        System.out.print("Select an option: ");
    }

    @Override
    public boolean processOption(int choice) {
        if (!facade.isTrainerAuthenticated()) {
            System.out.println("You must be logged in as a trainer to access this menu.");
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
            case TOGGLE_PROFILE_STATUS -> toggleProfileStatus();
            case BACK_TO_MAIN_MENU -> {
                return false;
            }
        }
        return true;
    }

    private Long getTrainerId() {
        return facade.getCurrentTrainerId();
    }

    private void viewProfile() {
        TrainerResponseDTO trainer = facade.getTrainerById(getTrainerId());
        System.out.println("\n===== Trainer Profile =====");
        System.out.println("ID: " + trainer.id());
        System.out.println("First Name: " + trainer.firstName());
        System.out.println("Last Name: " + trainer.lastName());
        System.out.println("Username: " + trainer.username());
        System.out.println("Training Type: " + trainer.trainingType().trainingTypeName());
        System.out.println("Status: " + (trainer.active() ? "Active" : "Inactive"));
    }

    private void updateProfile() {
        TrainerResponseDTO trainer = facade.getTrainerById(getTrainerId());

        System.out.println("\n===== Update Trainer Profile =====");
        System.out.println("Enter new details (press Enter to keep current values):");

        String firstName = getInput("First Name [" + trainer.firstName() + "]: ", trainer.firstName());
        String lastName = getInput("Last Name [" + trainer.lastName() + "]: ", trainer.lastName());
        Long trainingTypeId = selectTrainingType();

        TrainerRequestDTO requestDTO = new TrainerRequestDTO(firstName, lastName, trainer.username(), null, trainer.active(), trainingTypeId);
        TrainerResponseDTO updatedTrainer = facade.updateTrainerProfile(requestDTO);

        System.out.println("Profile updated successfully!");
        System.out.println("Updated Trainer ID: " + updatedTrainer.id());
    }

    private void changePassword() {
        System.out.println("\n----- Change Password -----");
        String newPassword = getInput("Enter new password: ", "");
        if (newPassword.isEmpty()) {
            System.out.println("Password cannot be empty.");
            return;
        }
        TrainerResponseDTO trainer = facade.getTrainerById(getTrainerId());
        facade.changeTrainerPassword(trainer.id(), newPassword);
        System.out.println("Password changed successfully!");
    }

    private void toggleProfileStatus() {
        System.out.println("\n----- Toggle Profile Status -----");
        boolean currentStatus = facade.getTrainerById(getTrainerId()).active();
        String newStatusText = currentStatus ? "inactive" : "active";

        System.out.println("\nYour profile is currently " + (currentStatus ? "active" : "inactive") + ".");
        String response = getInput("Do you want to make it " + newStatusText + "? (Y/N): ", "").toUpperCase();

        if (response.equals("Y")) {
            facade.setTrainerStatus(getTrainerId(), !currentStatus);
            System.out.println("Profile status updated successfully! Your profile is now " + newStatusText + ".");
        } else {
            System.out.println("Profile status remains unchanged.");
        }
    }

    private String getInput(String prompt, String defaultValue) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }

    private Long selectTrainingType() {
        System.out.println("\n----- Select Training Type -----");
        List<TrainingTypeResponseDTO> trainingTypes = facade.getTrainingTypes();
        trainingTypes.forEach(trainingType -> System.out.println(trainingType.id() + ": " + trainingType.trainingTypeName()));

        while (true) {
            System.out.print("Enter the ID of the training type: ");
            try {
                return Long.parseLong(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid ID.");
            }
        }
    }

    @Getter
    @RequiredArgsConstructor
    private enum MenuOption {
        VIEW_PROFILE(1, "View Profile"),
        UPDATE_PROFILE(2, "Update Profile"),
        CHANGE_PASSWORD(3, "Change Password"),
        TOGGLE_PROFILE_STATUS(4, "Activate/Deactivate Profile"),
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