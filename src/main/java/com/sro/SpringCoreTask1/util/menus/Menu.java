package com.sro.SpringCoreTask1.util.menus;

import java.util.Scanner;

public interface Menu {

    void displayMenu();

    boolean processOption(int choice);

    default boolean run() {
        Scanner scanner = new Scanner(System.in);
        boolean continueRunning = true;
        boolean returnToParent = true;

        while (continueRunning) {
            displayMenu();
            int choice = readIntInput(scanner);

            if (choice == -1) {
                System.out.println("Invalid input. Please enter a valid number.");
                continue;
            }

            try {
                continueRunning = processOption(choice);
                if (!continueRunning) {
                    returnToParent = (choice == 0); 
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }

        return returnToParent;
    }

    default int readIntInput(Scanner scanner) {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1; 
        }
    }
}