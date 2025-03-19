package com.sro.SpringCoreTask1.util.menus;

import java.util.Scanner;

import org.springframework.stereotype.Component;

import java.util.Optional;

import com.sro.SpringCoreTask1.facade.AuthServiceFacade;
import com.sro.SpringCoreTask1.util.menus.base.Menu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
public class LoginMenu implements Menu {

    private final Scanner scanner;
    private final AuthServiceFacade authFacade;

    public LoginMenu(AuthServiceFacade authFacade) {
        this.authFacade = authFacade;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void displayMenu() {
        System.out.println("\n===== Login Menu =====");
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
            case LOGIN_TRAINEE -> loginAsTrainee();
            case LOGIN_TRAINER -> loginAsTrainer();
            case BACK_TO_MAIN_MENU -> {
                return false;
            }
        }
        return false; 
    }

    private void loginAsTrainee() {
        System.out.println("\n===== Trainee Login =====");
        String username = getInput("Enter username: ");
        String password = getInput("Enter password: ");

        if (authFacade.authenticateTrainee(username, password)) {
            System.out.println("Login successful as Trainee!");
        } else {
            System.out.println("Login failed. Invalid credentials.");
        }
    }

    private void loginAsTrainer() {
        System.out.println("\n===== Trainer Login =====");
        String username = getInput("Enter username: ");
        String password = getInput("Enter password: ");

        if (authFacade.authenticateTrainer(username, password)) {
            System.out.println("Login successful as Trainer!");
        } else {
            System.out.println("Login failed. Invalid credentials.");
        }
    }

    private String getInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    @Getter
    @RequiredArgsConstructor
    private enum MenuOption {
        LOGIN_TRAINEE(1, "Login as Trainee"),
        LOGIN_TRAINER(2, "Login as Trainer"),
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