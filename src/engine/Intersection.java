package engine;

import vehicle.Vehicle;
import enums.VehicleType;

import java.util.Queue;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Intersection {

    private final ReentrantLock lock = new ReentrantLock(true); // fair lock
    private final Condition condition = lock.newCondition();

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
        lock.lock();
        try {
            enqueue(vehicle);

            while (!canEnter(vehicle)) {
                System.out.println(vehicle.getId() + " is waiting...");
                condition.await();
            }

            enter(vehicle);

        } catch (InterruptedException e) {
            removeFromQueue(vehicle);
            condition.signalAll();
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
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
        lock.lock();
        try {
            if (currentVehicles == 0) {
                return;
            }

            currentVehicles--;
            System.out.println(vehicle.getId() + " EXIT intersection");

            // đánh thức tất cả xe đang chờ
            condition.signalAll();

        } finally {
            lock.unlock();
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
        if (isPriority(vehicle)) {
            priorityQueue.remove(vehicle);
            return;
        }

        waitingQueue.remove(vehicle);
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
