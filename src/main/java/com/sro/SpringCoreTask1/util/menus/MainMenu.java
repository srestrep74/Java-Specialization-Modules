package com.sro.SpringCoreTask1.util.menus;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.sro.SpringCoreTask1.facade.SystemServiceFacade;

import lombok.RequiredArgsConstructor;


@Component
public class MainMenu implements Menu {

    private final SystemServiceFacade facade;
    private final TraineeMenu traineeMenu;
    private final TrainerMenu trainerMenu;
    private final LoginMenu loginMenu;
    private final RegistrationMenu registrationMenu;

    public MainMenu(SystemServiceFacade facade, TraineeMenu traineeMenu, TrainerMenu trainerMenu, LoginMenu loginMenu, RegistrationMenu registrationMenu) {
        this.facade = facade;
        this.traineeMenu = traineeMenu;
        this.trainerMenu = trainerMenu;
        this.loginMenu = loginMenu;
        this.registrationMenu = registrationMenu;
    }

    @Override
    public void displayMenu() {
        System.out.println("\n===== Fitness Management System =====");
        System.out.println("1. Registration");
        System.out.println("2. Login");
        System.out.println("0. Exit Application");
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
            case REGISTRATION -> registrationMenu.run();
            case LOGIN -> handleLogin();
            case EXIT -> {
                System.out.println("Goodbye!");
                return false;
            }
        }
        return true;
    }

    private void handleLogin() {
        loginMenu.run();
        if (facade.isTraineeAuthenticated()) {
            traineeMenu.run();
        } else if (facade.isTrainerAuthenticated()) {
            trainerMenu.run();
        } else {
            System.out.println("Login failed. Please try again.");
        }
    }

    @RequiredArgsConstructor
    private enum MenuOption {
        REGISTRATION(1, "Registration"),
        LOGIN(2, "Login"),
        EXIT(0, "Exit Application");

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