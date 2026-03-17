package vehicle;

import enums.Direction;
import enums.VehicleStatus;
import enums.VehicleType;

public class PriorityVehicle extends Vehicle {

    public PriorityVehicle(String id, VehicleType type, int speed, Direction direction) {
        super(id, type, speed, 10, direction);
    }

    @Override
    public void move() {
        status = VehicleStatus.MOVING;
        System.out.println(vehicleType + " " + id + " (priority) is moving");
    }

    @Override
    public boolean canPassRedLight() {
        return true;
    }
}