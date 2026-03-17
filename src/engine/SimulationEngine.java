package engine;

import config.AppConstants;
import enums.Direction;
import enums.LightColor;
import factory.VehicleFactory;
import queue.VehicleQueueManager;
import trafficlight.TrafficLight;
import vehicle.Vehicle;

import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Task 8: Engine điều khiển mô phỏng ở mức console.
 * Chức năng lõi: start/stop, sinh xe, tick đèn, xử lý xe qua giao lộ, thống kê cơ bản.
 */
public class SimulationEngine {

    private final VehicleFactory vehicleFactory;
    private final TrafficLight trafficLight;
    private final ScheduledExecutorService tickExecutor;
    private final AtomicBoolean isRunning;

    private final EnumMap<Direction, ConcurrentLinkedQueue<Vehicle>> waitingQueueByDirection;

    private final AtomicLong tickCount;
    private final AtomicLong totalGenerated;
    private final AtomicLong totalPassed;

    private volatile ScheduledFuture<?> tickTask;

    public SimulationEngine() {
        this(new VehicleFactory());
    }

    public SimulationEngine(VehicleFactory vehicleFactory) {
        this.vehicleFactory = Objects.requireNonNull(vehicleFactory, "vehicleFactory khong duoc null");
        this.trafficLight = new TrafficLight();
        this.tickExecutor = Executors.newSingleThreadScheduledExecutor();
        this.isRunning = new AtomicBoolean(false);

        this.waitingQueueByDirection = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.values()) {
            waitingQueueByDirection.put(direction, new ConcurrentLinkedQueue<>());
        }

        this.tickCount = new AtomicLong(0);
        this.totalGenerated = new AtomicLong(0);
        this.totalPassed = new AtomicLong(0);

        // Giữ kết nối Observer để không phá flow module đã có.
        this.trafficLight.addObserver(new VehicleQueueManager());
    }

    public boolean startSimulation() {
        if (!isRunning.compareAndSet(false, true)) {
            return false;
        }

        tickTask = tickExecutor.scheduleAtFixedRate(this::runOneTick, 0, AppConstants.SIMULATION_TICK, TimeUnit.SECONDS);
        log("Simulation started");
        return true;
    }

    public boolean stopSimulation() {
        if (!isRunning.compareAndSet(true, false)) {
            return false;
        }

        if (tickTask != null) {
            tickTask.cancel(false);
            tickTask = null;
        }
        log("Simulation stopped");
        return true;
    }

    public void shutdown() {
        stopSimulation();
        tickExecutor.shutdownNow();
    }

    public List<Vehicle> generateRandomVehicles(int count) {
        List<Vehicle> vehicles = vehicleFactory.createRandomVehicles(count);
        for (Vehicle vehicle : vehicles) {
            waitingQueueByDirection.get(vehicle.getDirection()).offer(vehicle);
            totalGenerated.incrementAndGet();
            log("Spawn " + vehicle.getVehicleType() + " #" + vehicle.getId() + " at " + vehicle.getDirection());
        }
        return vehicles;
    }

    public String getCurrentStats() {
        StringBuilder text = new StringBuilder();
        text.append("===== TRAFFIC STATS =====\n");
        text.append("Running: ").append(isRunning.get()).append("\n");
        text.append("Tick: ").append(tickCount.get()).append("\n");
        text.append("Light: ").append(trafficLight.getCurrentColor())
                .append(" (remain ").append(trafficLight.getRemainingTime()).append("s)\n");
        text.append("Generated: ").append(totalGenerated.get()).append("\n");
        text.append("Passed: ").append(totalPassed.get()).append("\n");
        text.append("Queue: ");
        for (Direction direction : Direction.values()) {
            text.append(direction).append("=").append(waitingQueueByDirection.get(direction).size()).append(" ");
        }
        text.append("\n=========================");
        return text.toString();
    }

    private void runOneTick() {
        if (!isRunning.get()) {
            return;
        }

        tickCount.incrementAndGet();
        trafficLight.tick();

        LightColor currentColor = trafficLight.getCurrentColor();
        log("Traffic Light: " + currentColor + " (remain " + trafficLight.getRemainingTime() + "s)");

        // Mỗi tick xử lý tối đa 1 xe mỗi hướng để flow dễ hiểu khi demo console.
        for (Direction direction : Direction.values()) {
            ConcurrentLinkedQueue<Vehicle> laneQueue = waitingQueueByDirection.get(direction);
            Vehicle firstVehicle = laneQueue.peek();
            if (firstVehicle == null) {
                continue;
            }

            if (firstVehicle.canMove(currentColor)) {
                Vehicle passedVehicle = laneQueue.poll();
                if (passedVehicle != null) {
                    passedVehicle.move();
                    totalPassed.incrementAndGet();
                    log(passedVehicle.getVehicleType() + " #" + passedVehicle.getId() + " passed intersection");
                }
            } else {
                log(firstVehicle.getVehicleType() + " #" + firstVehicle.getId() + " waiting at " + direction);
            }
        }
    }

    private void log(String message) {
        System.out.printf("[%02ds] %s%n", tickCount.get(), message);
    }
}
