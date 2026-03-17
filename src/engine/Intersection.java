package engine;

import vehicle.Vehicle;
import enums.VehicleType;

import java.util.Queue;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Objects;

public class Intersection {

    private final Object monitor = new Object();

    private int currentVehicles = 0;
    private static final int MAX_VEHICLES = 3;

    // Queue ưu tiên
    private final Queue<Vehicle> waitingQueue = new LinkedList<>();
    private final Queue<Vehicle> priorityQueue = new LinkedList<>();

    /**
     * Xe xin vào giao lộ
     */
    public void requestEntry(Vehicle vehicle) {
        Objects.requireNonNull(vehicle, "vehicle must not be null");
        synchronized (monitor) {
            enqueue(vehicle);

            while (!canEnter(vehicle)) {
                System.out.println(vehicle.getId() + " is waiting...");
                try {
                    monitor.wait();
                } catch (InterruptedException e) {
                    removeFromQueue(vehicle);
                    monitor.notifyAll();
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            enter(vehicle);
        }
    }

    /**
     * Xe vào giao lộ
     */
    private void enter(Vehicle vehicle) {
        currentVehicles++;

        removeFromQueue(vehicle);

        System.out.println(vehicle.getId() + " ENTER intersection");
    }

    /**
     * Xe rời giao lộ
     */
    public void exit(Vehicle vehicle) {
        if (vehicle == null) {
            System.out.println("Error: Vehicle cannot be null");
            return;
        }
        synchronized (monitor) {
            if (currentVehicles == 0) {
                return;
            }

            currentVehicles--;
            System.out.println(vehicle.getId() + " EXIT intersection");

            // đánh thức tất cả xe đang chờ
            monitor.notifyAll();
        }
    }

    /**
     * Điều kiện được vào
     */
    private boolean canEnter(Vehicle vehicle) {
        if (!isQueued(vehicle)) {
            return false;
        }

        // Full => không vào
        if (currentVehicles >= MAX_VEHICLES) {
            return false;
        }

        // Nếu có xe ưu tiên -> xe thường phải chờ
        if (hasWaitingPriorityVehicle() && !isPriority(vehicle)) {
            return false;
        }

        // FIFO trong từng queue
        if (isPriority(vehicle)) {
            return vehicle.equals(priorityQueue.peek());
        } else {
            return vehicle.equals(waitingQueue.peek());
        }
    }

    private boolean isPriority(Vehicle vehicle) {
        return vehicle.getVehicleType() == VehicleType.AMBULANCE;
    }

    private void enqueue(Vehicle vehicle) {
        if (isPriority(vehicle)) {
            priorityQueue.add(vehicle);
            return;
        }

        waitingQueue.add(vehicle);
    }

    private void removeFromQueue(Vehicle vehicle) {
    try {
        if (isPriority(vehicle)) {
            priorityQueue.remove(vehicle);
            return;
        }
        waitingQueue.remove(vehicle);
    } catch (NoSuchElementException e) {
        System.out.println("Warning: " + vehicle.getId() + " not found in queue");
        // Hoặc ghi log, hoặc continue mà không break
        }
    }

    private boolean isQueued(Vehicle vehicle) {
        if (isPriority(vehicle)) {
            return priorityQueue.contains(vehicle);
        }

        return waitingQueue.contains(vehicle);
    }

    private boolean hasWaitingPriorityVehicle() {
        return !priorityQueue.isEmpty();
    }
}
