package monitor;

import enums.VehicleType;
import vehicle.Vehicle;
import enums.Direction;
import queue.LaneQueue;

import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * TrafficMonitor collects simple statistics and prints structured logs.
 * - counts passed vehicles by type
 * - detects traffic jam when queue length exceeds threshold
 */
public class TrafficMonitor {

    private final EnumMap<VehicleType, AtomicInteger> passedByType = new EnumMap<>(VehicleType.class);
    private final AtomicInteger totalPassed = new AtomicInteger(0);
    private final AtomicInteger jamCount = new AtomicInteger(0);

    private final int jamThreshold;

    private Instant startTime = Instant.now();

    public TrafficMonitor(int jamThreshold) {
        this.jamThreshold = jamThreshold;
        for (VehicleType t : VehicleType.values()) {
            passedByType.put(t, new AtomicInteger(0));
        }
    }

    public void resetTimer() {
        startTime = Instant.now();
    }

    public long secondsSinceStart() {
        return Duration.between(startTime, Instant.now()).getSeconds();
    }

    public void onVehiclePassed(Vehicle v) {
        passedByType.get(v.getVehicleType()).incrementAndGet();
        totalPassed.incrementAndGet();
    }

    public void onQueueSizes(Map<?, Integer> sizesByLane) {
        // detect jam
        int max = sizesByLane.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        if (max >= jamThreshold) {
            int current = jamCount.incrementAndGet();
            System.out.printf("[%02ds] CANH BAO: Phat hien ket xe (size=%d). Tong so lan ket=%d\n", secondsSinceStart(), max, current);
        }
    }

    public void logWaiting(Vehicle v, Object lane) {
        System.out.printf("[%02ds] %s #%s dang doi o lan %s\n", secondsSinceStart(), v.getVehicleType(), v.getId(), lane);
    }

    public void logPassed(Vehicle v) {
        System.out.printf("[%02ds] %s #%s da di vao giao lo\n", secondsSinceStart(), v.getVehicleType(), v.getId());
    }

    /**
     * Produce waiting vehicles breakdown by type using Stream API over lane snapshots.
     */
     public void reportWaitingByType(Map<Direction, LaneQueue<Vehicle>> lanes) {
         Map<VehicleType, Long> waitingCountByType = lanes.values().stream()
                 .flatMap(l -> l.stream())
                 .collect(Collectors.groupingBy(Vehicle::getVehicleType, Collectors.counting()));

         String perType = waitingCountByType.entrySet().stream()
                 .map(e -> e.getKey() + "=" + e.getValue())
                 .collect(Collectors.joining(", "));

        System.out.printf("[%02ds] So luong dang doi theo loai: %s\n", secondsSinceStart(), perType);
     }

    public void printReport() {
        System.out.println("===== BAO CAO GIAO THONG =====");
        System.out.println("Thoi gian (s): " + secondsSinceStart());
        System.out.println("Tong so da di qua: " + totalPassed.get());
        System.out.println("So luong di qua theo loai:");
        String perType = passedByType.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(", "));
        System.out.println(perType);
        System.out.println("Tong so lan ket: " + jamCount.get());
        System.out.println("===============================");
    }
}
