package engine;

import factory.VehicleFactory;
import vehicle.Vehicle;

import java.util.List;
import java.util.Objects;

/**
 * Engine chỉ điều phối sinh xe thông qua VehicleFactory.
 * Tránh khởi tạo trực tiếp StandardVehicle/PriorityVehicle trong engine.
 */
public class SimulationEngine {

    private final VehicleFactory vehicleFactory;

    public SimulationEngine() {
        this(new VehicleFactory());
    }

    public SimulationEngine(VehicleFactory vehicleFactory) {
        this.vehicleFactory = Objects.requireNonNull(vehicleFactory, "vehicleFactory không được null");
    }

    public Vehicle spawnRandomVehicle() {
        return vehicleFactory.createRandomVehicle();
    }

    public List<Vehicle> spawnRandomVehicles(int count) {
        return vehicleFactory.createRandomVehicles(count);
    }
}
