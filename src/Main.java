//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
// Updated Main.java to connect console menu with SimulationEngine for Task 8: start/stop simulation, generate vehicles, show stats, exit; added safe input handling and Vietnamese comments in confusing parts.
import engine.SimulationEngine;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SimulationEngine simulationEngine = new SimulationEngine();
        boolean isMenuRunning = true;

        while (isMenuRunning) {
            printMenu();
            int userChoice = readIntInput(scanner, "Chon chuc nang: ");

            switch (userChoice) {
                case 1:
                    if (simulationEngine.startSimulation()) {
                        System.out.println("Da bat dau mo phong.");
                    } else {
                        System.out.println("Mo phong dang chay roi.");
                    }
                    break;
                case 2:
                    if (simulationEngine.stopSimulation()) {
                        System.out.println("Da dung mo phong.");
                    } else {
                        System.out.println("Mo phong hien dang khong chay.");
                    }
                    break;
                case 3:
                    int vehicleCount = readIntInput(scanner, "Nhap so xe muon sinh ngau nhien: ");
                    if (vehicleCount < 0) {
                        System.out.println("So luong xe phai >= 0.");
                        break;
                    }
                    simulationEngine.generateRandomVehicles(vehicleCount);
                    System.out.println("Da tao " + vehicleCount + " xe ngau nhien vao queue.");
                    break;
                case 4:
                    System.out.println(simulationEngine.getCurrentStats());
                    break;
                case 5:
                    isMenuRunning = false;
                    break;
                default:
                    System.out.println("Lua chon khong hop le. Vui long thu lai.");
            }
        }

        // Luon shutdown executor khi thoat de tranh treo process nen.
        simulationEngine.shutdown();
        scanner.close();
        System.out.println("Da thoat chuong trinh.");
    }

    private static void printMenu() {
        System.out.println("\n===== TRAFFIC SIMULATION MENU =====");
        System.out.println("1. Start simulation");
        System.out.println("2. Stop simulation");
        System.out.println("3. Generate random vehicles");
        System.out.println("4. Show current stats");
        System.out.println("5. Exit");
    }

    private static int readIntInput(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            String inputText = scanner.nextLine().trim();
            try {
                return Integer.parseInt(inputText);
            } catch (NumberFormatException ex) {
                System.out.println("Vui long nhap so nguyen hop le.");
            }
        }
    }
}