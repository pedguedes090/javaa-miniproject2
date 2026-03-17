//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
// Updated Main.java to include a basic menu structure for the project framework.
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\nTraffic Simulation Menu:");
            System.out.println("1. Start simulation");
            System.out.println("2. Stop simulation");
            System.out.println("3. Generate random vehicles");
            System.out.println("4. Show traffic stats");
            System.out.println("5. Show waiting queues");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    System.out.println("Starting simulation...");
                    // Placeholder for starting simulation logic
                    break;
                case 2:
                    System.out.println("Stopping simulation...");
                    // Placeholder for stopping simulation logic
                    break;
                case 3:
                    System.out.println("Generating random vehicles...");
                    // Placeholder for vehicle generation logic
                    break;
                case 4:
                    System.out.println("Showing traffic stats...");
                    // Placeholder for traffic stats logic
                    break;
                case 5:
                    System.out.println("Showing waiting queues...");
                    // Placeholder for waiting queue logic
                    break;
                case 6:
                    System.out.println("Exiting application. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }
}