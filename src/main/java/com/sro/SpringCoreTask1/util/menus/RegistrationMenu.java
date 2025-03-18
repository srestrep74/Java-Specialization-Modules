package com.sro.SpringCoreTask1.util.menus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.sro.SpringCoreTask1.dto.request.TraineeRequestDTO;
import com.sro.SpringCoreTask1.dto.request.TrainerRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TraineeResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainerResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingTypeResponseDTO;
import com.sro.SpringCoreTask1.facade.SystemServiceFacade;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
public class RegistrationMenu implements Menu {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final Scanner scanner;
    private final SystemServiceFacade facade;

    public RegistrationMenu(SystemServiceFacade facade) {
        this.scanner = new Scanner(System.in);
        this.facade = facade;
    }

    @Override
    public void displayMenu() {
        System.out.println("\n===== Registration Menu =====");
        for (MenuOption option : MenuOption.values()) {
            System.out.println(option.getOption() + ". " + option.getDescription());
        }
        System.out.print("Select an option: ");
    }

    @Override
    public boolean processOption(int choice) {
        Optional<MenuOption> selectedOption = MenuOption.fromOption(choice);
        if (selectedOption.isEmpty()) {
            System.out.println("Invalid option. Please try again.");
            return true;
        }

        switch (selectedOption.get()) {
            case REGISTER_TRAINEE -> registerTrainee();
            case REGISTER_TRAINER -> registerTrainer();
            case BACK_TO_MAIN_MENU -> {
                return false;
            }
        }
        return true;
    }

    private void registerTrainee() {
        System.out.println("\n===== Trainee Registration =====");

        String firstName = getInput("Enter first name: ", "");
        String lastName = getInput("Enter last name: ", "");
        String username = getInput("Enter username: ", "");
        String password = getInput("Enter password: ", "");
        String address = getInput("Enter address: ", "");
        LocalDate dateOfBirth = getDateInput("Enter date of birth (YYYY-MM-DD): ");

        try {
            TraineeRequestDTO trainee = new TraineeRequestDTO(firstName, lastName, username, password, true, address, dateOfBirth, null);
            TraineeResponseDTO savedTrainee = facade.createTraineeProfile(trainee);
            System.out.println("Registration successful! Trainee ID: " + savedTrainee.id());
            System.out.println("You can now login with your credentials.");
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private void registerTrainer() {
        System.out.println("\n===== Trainer Registration =====");

        String firstName = getInput("Enter first name: ", "");
        String lastName = getInput("Enter last name: ", "");
        String username = getInput("Enter username: ", "");
        String password = getInput("Enter password: ", "");
        Long trainingTypeId = selectTrainingType();

        try {
            TrainerRequestDTO trainer = new TrainerRequestDTO(firstName, lastName, username, password, true, trainingTypeId);
            TrainerResponseDTO savedTrainer = facade.createTrainerProfile(trainer);
            System.out.println("Registration successful! Trainer ID: " + savedTrainer.id());
            System.out.println("You can now login with your credentials.");
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
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

    private String getInput(String prompt, String defaultValue) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }

    private LocalDate getDateInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String dateStr = scanner.nextLine().trim();
            try {
                return LocalDate.parse(dateStr, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }
    }

    @Getter
    @RequiredArgsConstructor
    private enum MenuOption {
        REGISTER_TRAINEE(1, "Register as Trainee"),
        REGISTER_TRAINER(2, "Register as Trainer"),
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