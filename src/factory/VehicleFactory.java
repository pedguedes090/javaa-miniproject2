package factory;

import enums.Direction;
import enums.VehicleType;
import vehicle.PriorityVehicle;
import vehicle.StandardVehicle;
import vehicle.Vehicle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Factory sinh phương tiện ngẫu nhiên cho hệ mô phỏng.
 * Có thể cấu hình tỉ lệ sinh từng loại xe và dải tốc độ.
 */
public class VehicleFactory {

    private static final int DEFAULT_MIN_SPEED = 20;
    private static final int DEFAULT_MAX_SPEED = 80;

    private final Random random;
    private final AtomicLong sequence = new AtomicLong(0);
    private final EnumMap<VehicleType, Integer> spawnWeights;
    private final int minSpeed;
    private final int maxSpeed;

    public VehicleFactory() {
        this(defaultWeights(), DEFAULT_MIN_SPEED, DEFAULT_MAX_SPEED, new Random());
    }

    public VehicleFactory(Map<VehicleType, Integer> spawnWeights) {
        this(spawnWeights, DEFAULT_MIN_SPEED, DEFAULT_MAX_SPEED, new Random());
    }

    public VehicleFactory(Map<VehicleType, Integer> spawnWeights, int minSpeed, int maxSpeed) {
        this(spawnWeights, minSpeed, maxSpeed, new Random());
    }

    public VehicleFactory(Map<VehicleType, Integer> spawnWeights, int minSpeed, int maxSpeed, Random random) {
        validateSpeedRange(minSpeed, maxSpeed);
        Objects.requireNonNull(random, "random không được null");

        this.spawnWeights = sanitizeWeights(spawnWeights);
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.random = random;
    }

    public Vehicle createRandomVehicle() {
        VehicleType selectedType = pickVehicleTypeByWeight();
        Direction randomDirection = pickRandomDirection();
        int randomSpeed = pickRandomSpeed();
        int randomPriority = pickRandomPriority(selectedType);
        String vehicleId = generateVehicleId(selectedType);

        return buildVehicle(vehicleId, selectedType, randomSpeed, randomDirection, randomPriority);
    }

    public List<Vehicle> createRandomVehicles(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("số lượng xe phải >= 0");
        }

        List<Vehicle> vehicles = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            vehicles.add(createRandomVehicle());
        }
        return vehicles;
    }

    public Map<VehicleType, Integer> getSpawnWeights() {
        return Collections.unmodifiableMap(spawnWeights);
    }

    private Vehicle buildVehicle(String id, VehicleType type, int speed, Direction direction, int priority) {
        if (type == VehicleType.AMBULANCE) {
            return new RandomizedPriorityVehicle(id, type, speed, direction, priority);
        }
        return new RandomizedStandardVehicle(id, type, speed, direction, priority);
    }

    private VehicleType pickVehicleTypeByWeight() {
        int totalWeight = 0;
        for (Integer weight : spawnWeights.values()) {
            totalWeight += weight;
        }

        if (totalWeight <= 0) {
            throw new IllegalStateException("tổng trọng số sinh xe phải > 0");
        }

        int cursor = random.nextInt(totalWeight);
        int cumulative = 0;
        for (Map.Entry<VehicleType, Integer> entry : spawnWeights.entrySet()) {
            cumulative += entry.getValue();
            if (cursor < cumulative) {
                return entry.getKey();
            }
        }

        return VehicleType.CAR;
    }

    private Direction pickRandomDirection() {
        Direction[] directions = Direction.values();
        return directions[random.nextInt(directions.length)];
    }

    private int pickRandomSpeed() {
        return random.nextInt(maxSpeed - minSpeed + 1) + minSpeed;
    }

    private int pickRandomPriority(VehicleType type) {
        switch (type) {
            case MOTORBIKE:
                return randomInRange(1, 2);
            case CAR:
                return randomInRange(2, 4);
            case TRUCK:
                return randomInRange(3, 5);
            case AMBULANCE:
                return randomInRange(8, 10);
            default:
                return 1;
        }
    }

    private int randomInRange(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    private String generateVehicleId(VehicleType type) {
        long next = sequence.incrementAndGet();
        return type.name().charAt(0) + String.format("%04d", next);
    }

    private static EnumMap<VehicleType, Integer> defaultWeights() {
        EnumMap<VehicleType, Integer> defaults = new EnumMap<>(VehicleType.class);
        defaults.put(VehicleType.MOTORBIKE, 40);
        defaults.put(VehicleType.CAR, 35);
        defaults.put(VehicleType.TRUCK, 20);
        defaults.put(VehicleType.AMBULANCE, 5);
        return defaults;
    }

    private static EnumMap<VehicleType, Integer> sanitizeWeights(Map<VehicleType, Integer> weights) {
        EnumMap<VehicleType, Integer> sanitized = new EnumMap<>(VehicleType.class);

        if (weights == null || weights.isEmpty()) {
            sanitized.putAll(defaultWeights());
            return sanitized;
        }

        for (VehicleType type : VehicleType.values()) {
            int weight = weights.getOrDefault(type, 0);
            if (weight < 0) {
                throw new IllegalArgumentException("trọng số của " + type + " phải >= 0");
            }
            sanitized.put(type, weight);
        }

        return sanitized;
    }

    private static void validateSpeedRange(int minSpeed, int maxSpeed) {
        if (minSpeed <= 0 || maxSpeed <= 0) {
            throw new IllegalArgumentException("khoảng tốc độ phải là số dương");
        }
        if (minSpeed > maxSpeed) {
            throw new IllegalArgumentException("minSpeed phải <= maxSpeed");
        }
    }

    /**
     * Subclass nội bộ để giữ nguyên model hiện tại nhưng cho phép cấu hình priority ngẫu nhiên.
     */
    private static class RandomizedStandardVehicle extends StandardVehicle {
        RandomizedStandardVehicle(String id, VehicleType type, int speed, Direction direction, int priority) {
            super(id, type, speed, direction);
            this.priority = priority;
        }
    }

    /**
     * Subclass nội bộ để giữ canPassRedLight của xe ưu tiên và vẫn random được priority.
     */
    private static class RandomizedPriorityVehicle extends PriorityVehicle {
        RandomizedPriorityVehicle(String id, VehicleType type, int speed, Direction direction, int priority) {
            super(id, type, speed, direction);
            this.priority = priority;
        }
    }
}
