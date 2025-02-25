package com.sro.SpringCoreTask1;

import java.util.Scanner;

import org.springframework.stereotype.Component;

import com.sro.SpringCoreTask1.util.menus.TraineeMenu;
import com.sro.SpringCoreTask1.util.menus.TrainerMenu;
import com.sro.SpringCoreTask1.util.menus.TrainingMenu;

@Component
public class ConsoleMenu {
    
    private final TraineeMenu traineeMenu;
    private final TrainerMenu trainerMenu;
    private final TrainingMenu trainingMenu;

    private ConsoleMenu(TraineeMenu traineeMenu, TrainerMenu trainerMenu, TrainingMenu trainingMenu) {
        this.traineeMenu = traineeMenu;
        this.trainerMenu = trainerMenu;
        this.trainingMenu = trainingMenu;
    }

    public void run(){
        Scanner scanner = new Scanner(System.in);
        boolean runnning = true;

        while(runnning) {
            System.out.println("=== Main Menu ===");
            System.out.println("1. Trainee Management Menu");
            System.out.println("2. Trainer Management Menu");
            System.out.println("3. Training Management Menu");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 : 
                    this.traineeMenu.showMenu(scanner);
                    break;
                case 2 :
                    this.trainerMenu.showMenu(scanner);
                    break;
                case 3 :
                    this.trainingMenu.showMenu(scanner);
                    break;
                case 4 :
                    runnning = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }
}
